package by.niaprauski.player

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import by.niaprauski.domain.models.AppSettings
import by.niaprauski.domain.models.Track
import by.niaprauski.domain.usecases.settings.GetSettingsFlowUseCase
import by.niaprauski.domain.usecases.settings.SetWelcomeMessageStatusUseCase
import by.niaprauski.domain.usecases.track.ChangeTrackFavoriteUseCase
import by.niaprauski.domain.usecases.track.FilterAndSaveTracksUseCase
import by.niaprauski.domain.usecases.track.GetTracksForPlayUseCase
import by.niaprauski.domain.usecases.track.SetTrackFavoriteUpUseCase
import by.niaprauski.player.mapper.TrackModelMapper
import by.niaprauski.player.models.PAction
import by.niaprauski.player.models.PlayerEvent
import by.niaprauski.player.models.PlayerState
import by.niaprauski.player.models.SyncTrackStatus
import by.niaprauski.playerservice.PlayerService
import by.niaprauski.playerservice.PlayerServiceConnection
import by.niaprauski.playerservice.models.ExoPlayerState
import by.niaprauski.utils.media.MediaHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@UnstableApi
@HiltViewModel(assistedFactory = PlayerViewModel.Factory::class)
class PlayerViewModel @AssistedInject constructor(
    @Assisted("radioTrack") val radioTrack: Uri? = null,
    @Assisted("singleAudioTrack") val singleAudioTrack: Uri? = null,
    private val application: Application,
    private val filterAndSaveTracksUseCase: FilterAndSaveTracksUseCase,
    private val getTracksForPlayUseCase: GetTracksForPlayUseCase,
    private val getSettingsFlowUseCase: GetSettingsFlowUseCase,
    private val setWelcomeMessageStatusUseCase: SetWelcomeMessageStatusUseCase,
    private val setTrackFavoriteUpUseCase: SetTrackFavoriteUpUseCase,
    private val changeTrackFavoriteUseCase: ChangeTrackFavoriteUseCase,
    private val trackModelMapper: TrackModelMapper,
) : ViewModel() {

    @dagger.assisted.AssistedFactory
    interface Factory {
        fun create(
            @Assisted("radioTrack") radioTrack: Uri?,
            @Assisted("singleAudioTrack") singleAudioTrack: Uri?,
        ): PlayerViewModel
    }

    private val serviceConnection = PlayerServiceConnection(application)
    val playerService: StateFlow<PlayerService?> = serviceConnection.service.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _state = MutableStateFlow<PlayerState>(PlayerState.DEFAULT)
    val state = _state.asStateFlow()

    private var appSettings: AppSettings? = null

    val exoPlayerState: StateFlow<ExoPlayerState> = playerService.flatMapLatest { service ->
        service?.state ?: flowOf(ExoPlayerState.DEFAULT)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExoPlayerState.DEFAULT
    )

    private val _event by lazy { Channel<PlayerEvent>() }
    val event: Flow<PlayerEvent> by lazy { _event.receiveAsFlow() }

    private fun playInitialTrack() {
        viewModelScope.launch {
            playerService.filterNotNull().first()
            when {
                radioTrack == null && singleAudioTrack == null -> loadTracks()
                radioTrack != null -> playRadioTrack(radioTrack)
                singleAudioTrack != null -> playSingleAudioTrack(singleAudioTrack)
            }
        }
    }

    init {
        getSettings()
    }

    fun onCreate() {
        if (playerService.value != null) return

        serviceConnection.bind()
        startPlayerService(application)
        playInitialTrack()
    }

    fun onAction(action: PAction) {
        when (action) {
            is PAction.Play -> play()
            is PAction.Pause -> pause()
            is PAction.Stop -> stop()
            is PAction.PlayNext -> playNext()
            is PAction.PlayPrevious -> playPrevious()
            is PAction.ChangeShuffleMode -> changeShuffleMode()
            is PAction.ChangeRepeatMode -> changeRepeatMode()
            is PAction.RequestSync -> requestSync()
            is PAction.ShowPermissionInformationDialog -> showPermissionInformationDialog()
            is PAction.HideWelcomeDialogs -> hideWelcomeDialogs()
            is PAction.HideMediaPermissionInfoDialog -> hideMediaPermissionInfoDialog()
            is PAction.ShowPlayList -> showPlayList()
            is PAction.HidePlayList -> hidePlayList()
            is PAction.ReloadPlayList -> reloadPlayList()
            is PAction.SeekTo -> seekTo(action.position)
            is PAction.UpTrackFavorite -> upTrackFavorite(action.trackId)
            is PAction.ChangeTrackFavorite -> changeTrackFavorite(action.trackId)
            is PAction.RemoveTrackFromPlayList -> removeTrackFromPlayList(action.trackId)
            is PAction.PlayTrackFromPlayList -> playTrackFromPlayList(action.trackId)
            is PAction.SyncTracks -> syncTracks(action.status)
        }
    }

    private fun loadTracks() {
        viewModelScope.launch {
            getTracksForPlayUseCase.invoke()
                .onSuccess { items ->
                    handleSuccessLoadTracks(items)
                }
                .onFailure {
                    //TODO add get track failure information
                }
        }
    }

    private suspend fun handleSuccessLoadTracks(items: List<Track>) {
        setPlayList(trackModelMapper.toMediaItems(items))
        appSettings?.isAutoPlayOnLaunch?.let { isAutoPlay ->
            if (isAutoPlay) _event.send(PlayerEvent.Play)
        }
    }

    private fun syncTracks(syncTracks: SyncTrackStatus) {
        if (syncTracks !is SyncTrackStatus.Finished) {
            _state.update { it.copy(isSyncing = true) }
            return
        } else viewModelScope.launch { saveTracks(syncTracks) }
    }

    private suspend fun CoroutineScope.saveTracks(syncTracks: SyncTrackStatus.Finished) {

        val saveJob = launch {
            val syncTracks = trackModelMapper.toDomainModels(syncTracks.tracks)
            filterAndSaveTracksUseCase.invoke(syncTracks)
                .onSuccess {
                    handleSyncedTracks()
                    _event.send(PlayerEvent.MediaItemSynced)
                }
                .onFailure {
                    _event.send(PlayerEvent.MediaItemSyncError)
                }
        }

        val animationJob = launch { delay(MIN_SYNC_TIME) }

        saveJob.join()
        animationJob.join()
        _state.update { it.copy(isSyncing = false) }
    }

    private fun handleSyncedTracks() {
        if (_state.value.trackCount == 0) loadTracks()
    }

    private fun play() {
        viewModelScope.launch {
            _event.send(PlayerEvent.Play)
        }
    }

    private fun pause() {
        viewModelScope.launch {
            _event.send(PlayerEvent.Pause)
        }
    }

    private fun stop() {
        viewModelScope.launch {
            _event.send(PlayerEvent.Stop)
        }
    }

    private fun playNext() {
        viewModelScope.launch {
            _event.send(PlayerEvent.PlayNext)
        }
    }

    private fun playPrevious() {
        viewModelScope.launch {
            _event.send(PlayerEvent.PlayPrevious)
        }
    }

    private fun setPlayList(tracks: List<MediaItem>) {
        viewModelScope.launch {
            _state.update { it.copy(trackCount = tracks.size) }
            _event.send(PlayerEvent.SetPlayList(tracks))
        }
    }

    private fun changeShuffleMode() {
        viewModelScope.launch {
            _event.send(PlayerEvent.ChangeShuffleMode)
        }
    }

    private fun changeRepeatMode() {
        viewModelScope.launch {
            _event.send(PlayerEvent.ChangeRepeatMode)
        }
    }

    private fun playSingleAudioTrack(uri: Uri) {
        if (uri.toString().isEmpty()) return
        viewModelScope.launch {
            val mediaItem = MediaHandler.uriToMediaItemFromIntent(uri, application.contentResolver)
            _event.send(PlayerEvent.PlaySingleTrack(mediaItem))
        }
    }

    private fun playRadioTrack(uri: Uri) {
        if (uri.toString().isEmpty()) return
        viewModelScope.launch {
            MediaHandler.radioUriToMediaItem(uri, application.contentResolver)?.let { mediaItem ->
                _event.send(PlayerEvent.PlaySingleTrack(mediaItem))
            }
        }
    }

    private fun requestSync() {
        viewModelScope.launch {
            setWelcomeMessageStatusUseCase.setFirstLaunchStatus(false)
            _event.send(PlayerEvent.SyncPlayList)
        }
    }

    private fun seekTo(position: Float) {
        viewModelScope.launch {
            _event.send(PlayerEvent.SeekTo(position))
        }
    }

    private fun showPermissionInformationDialog() {
        viewModelScope.launch {
            _state.update { it.copy(isShowPermissionInformationDialog = true) }
        }
    }

    private fun hideWelcomeDialogs() {
        viewModelScope.launch {
            setWelcomeMessageStatusUseCase.setFirstLaunchStatus(false)
            _state.update { it.copy(isShowWelcomeDialog = false) }
        }
    }

    private fun hideMediaPermissionInfoDialog() {
        viewModelScope.launch {
            _state.update { it.copy(isShowPermissionInformationDialog = false) }
        }
    }

    private fun upTrackFavorite(trackId: String) {
        if (playerService.value == null) return

        viewModelScope.launch {
            setTrackFavoriteUpUseCase.invoke(trackId)
            _event.send(PlayerEvent.UpFavorite(trackId))
        }
    }

    private fun changeTrackFavorite(trackId: String) {
        if (playerService.value == null) return

        viewModelScope.launch {
            changeTrackFavoriteUseCase.invoke(trackId)
            _event.send(PlayerEvent.ChangeFavorite(trackId))
        }
    }

    private fun hidePlayList() {
        viewModelScope.launch {
            _state.update { it.copy(isShowPlayList = false) }
        }
    }

    private fun showPlayList() {
        viewModelScope.launch {
            _state.update { it.copy(isShowPlayList = true) }
        }
    }

    private fun removeTrackFromPlayList(trackId: String) {
        viewModelScope.launch {
            _event.send(PlayerEvent.RemoveTrackFromPlayList(trackId))
        }
    }

    private fun playTrackFromPlayList(trackId: String) {
        viewModelScope.launch {
            _event.send(PlayerEvent.PlayTrackFromPlayList(trackId))
        }
    }

    private fun reloadPlayList() {
        viewModelScope.launch {
            getTracksForPlayUseCase.invoke()
                .onSuccess { items ->
                    setPlayList(trackModelMapper.toMediaItems(items))
                    _event.send(PlayerEvent.PlaylistChanged)
                }
        }
    }

    private fun getSettings() {
        viewModelScope.launch {
            getSettingsFlowUseCase.invoke().collect { settings ->
                _state.update {
                    it.copy(
                        isShowWelcomeDialog = settings.isShowWelcomeMessage,
                        isVisuallyEnabled = settings.isVisuallyEnabled
                    )
                }
                appSettings = settings
            }
        }
    }

    override fun onCleared() {
        serviceConnection.unbind()
        super.onCleared()
    }

    private fun startPlayerService(context: Context) {
        val intent = Intent(context, PlayerService::class.java)
        context.startService(intent)
    }

    companion object {
        private const val MIN_SYNC_TIME = 500L
    }
}
