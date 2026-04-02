package by.niaprauski.playerservice

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import by.niaprauski.domain.usecases.settings.GetSettingsFlowUseCase
import by.niaprauski.playerservice.models.ExoPlayerState
import by.niaprauski.playerservice.models.PlayerServiceAction
import by.niaprauski.playerservice.models.TrackProgress
import by.niaprauski.playerservice.models.TrackShort
import by.niaprauski.playerservice.utils.NotificationCreator
import by.niaprauski.playerservice.utils.SoundProcessor
import by.niaprauski.playerservice.utils.getMediaItemIndex
import by.niaprauski.translations.R
import by.niaprauski.utils.constants.EMPTY_FLOW_ARRAY
import by.niaprauski.utils.constants.TEXT_EMPTY
import by.niaprauski.utils.extension.UNKNOWN_TRACK_ID
import by.niaprauski.utils.extension.fixOldEncoding
import by.niaprauski.utils.extension.getFileName
import by.niaprauski.utils.extension.ifNullOrEmpty
import by.niaprauski.utils.extension.orDefault
import by.niaprauski.utils.extension.toTrackTime
import by.niaprauski.utils.media.ITrackShort
import by.niaprauski.utils.models.TRACK_KEY_FAVORITE
import by.niaprauski.utils.models.TRACK_KEY_FILE_NAME
import by.niaprauski.utils.models.TRACK_KEY_ID
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class PlayerService : MediaSessionService() {

    @Inject
    lateinit var getSettingsFlowUseCase: Lazy<GetSettingsFlowUseCase>

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var progressTrackingJob: Job? = null
    private val playerBinder = PlayerBinder()
    private val notificationId = 5465

    private val _state = MutableStateFlow(ExoPlayerState.DEFAULT)
    val state = _state.asStateFlow()

    private val _trackProgress = MutableStateFlow(TrackProgress.DEFAULT)
    val trackProgress = _trackProgress.asStateFlow()
    private val _waveform = MutableStateFlow(EMPTY_FLOW_ARRAY)
    val waveform = _waveform.asStateFlow()

    private val _playList = MutableStateFlow<List<ITrackShort>>(emptyList())
    val playList = _playList.asStateFlow()

    private val soundProcessor = SoundProcessor(serviceScope, _waveform)


    inner class PlayerBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this, createRenderFactory())
            .build().apply {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build()
                addListener(playerListener)
                repeatMode = Player.REPEAT_MODE_ALL
                setAudioAttributes(audioAttributes, true)
                addAnalyticsListener(EventLogger()) //TODO remove/to debug
            }
        player?.let { player ->
            mediaSession = MediaSession.Builder(this, player).build()
        }

        observeAppSettings()
    }

    private fun observeAppSettings() {
        serviceScope.launch {
            getSettingsFlowUseCase.get().invoke().collectLatest { settings ->
                soundProcessor.setIsVisuallyEnabled(settings.isVisuallyEnabled)
            }
        }
    }

    private fun createRenderFactory(): DefaultRenderersFactory {

        val audioSink = DefaultAudioSink.Builder(this)
            .setAudioProcessors(arrayOf(soundProcessor))
            .build()

        return object : DefaultRenderersFactory(this) {
            override fun buildAudioSink(
                context: Context,
                enableFloatOutput: Boolean,
                enableAudioOutputPlaybackParams: Boolean
            ): AudioSink = audioSink
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onBind(intent: Intent?): IBinder {
        return playerBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            PlayerServiceAction.PLAY.name -> play()
            PlayerServiceAction.PAUSE.name -> pause()
            PlayerServiceAction.STOP.name -> stop()
            PlayerServiceAction.PREVIOUS_TRACK.name -> seekToPrevious()
            PlayerServiceAction.NEXT_TRACK.name -> seekToNext()

            else -> {
                //do nothing
            }

        }

        val notification = NotificationCreator().buildNotification(
            context = this,
            player = player,
            mediaSession = mediaSession,
        )
        startForeground(notificationId, notification)


        return START_STICKY
    }

    override fun onDestroy() {
        player?.removeListener(playerListener)
        player?.release()
        player = null
        mediaSession?.release()
        mediaSession = null
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCreator().buildNotification(
            context = this,
            player = player,
            mediaSession = mediaSession,
        )

        notificationManager.notify(notificationId, notification)
    }

    fun setTracks(tracks: List<MediaItem>) {

        player?.apply {
            setMediaItems(tracks, 0, 0)
            prepare()
        }
    }

    fun setTrack(track: MediaItem) {
        stopWithClearMediaItems()
        playSingleMediaItem(track)
    }

    private fun stopWithClearMediaItems() {
        player?.stop()
        player?.clearMediaItems()
    }

    private fun playSingleMediaItem(mediaItem: MediaItem) {
        val playList = listOf(mediaItem)
        player?.apply {
            setMediaItems(playList, 0, 0)
            prepare()
            play()
        }
    }

    fun play() {
        if (player?.playbackState == Player.STATE_IDLE) {
            player?.prepare()
        }
        player?.play()
    }

    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.pause()
        player?.seekTo(0)
    }

    fun seekToNext() {
        player?.seekToNextMediaItem()
    }

    fun seekToPrevious() {
        player?.seekToPreviousMediaItem()
    }

    fun seekTo(progress: Float) {
        val player = player ?: return

        if (player.playbackState == Player.STATE_IDLE) {
            player.prepare()
        }

        val position = (getDuration() * progress).toLong()
        player.seekTo(position)
    }

    fun getCurrentPosition(): Long {
        return player?.currentPosition ?: 0
    }

    fun getDuration(): Long {
        return player?.duration ?: 0
    }

    fun playWithPosition(item: MediaItem) {

        val index = player?.getMediaItemIndex(item) ?: -1
        if (index != -1) {
            player?.seekTo(index, 0)
        } else {
            player?.addMediaItem(item)
            playTrackFromPlayList(item.mediaId)
        }
    }

    fun changeShuffleMode() {
        player?.let { it.shuffleModeEnabled = !it.shuffleModeEnabled }
    }

    fun changeRepeatMode() {
        player?.let {
            val newRepeatMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_OFF
                else -> Player.REPEAT_MODE_OFF
            }
            it.repeatMode = newRepeatMode
        }
    }


    fun removeMediaItem(item: MediaItem) {
        val index = player?.getMediaItemIndex(item) ?: -1
        if (index == -1) return
        player?.removeMediaItem(index)
    }


    fun removeTrackFromPlayList(trackId: String) {
        val player = player ?: return

        for (i in 0 until player.mediaItemCount) {
            val mediaItem = player.getMediaItemAt(i)
            if (mediaItem.mediaId == trackId) {
                player.removeMediaItem(i)
                break
            }
        }
    }

    fun playTrackFromPlayList(trackId: String) {
        val player = player ?: return

        for (i in 0 until player.mediaItemCount) {
            if (player.getMediaItemAt(i).mediaId == trackId) {
                player.seekTo(i, 0)
                player.play()
                break
            }
        }
    }


    fun addItemToPlayList(item: MediaItem) {
        val index = player?.getMediaItemIndex(item) ?: -1
        if (index != -1) return
        player?.addMediaItem(item)
    }

    fun upTrackFavorite(trackId: String) {
        val currentTrack = player?.currentMediaItem ?: return
        val id = currentTrack.mediaMetadata.extras?.getString(TRACK_KEY_ID) ?: return
        val favorite = currentTrack.mediaMetadata.extras?.getInt(TRACK_KEY_FAVORITE) ?: return

        if (id == trackId) {
            val favorite = favorite + 1
            currentTrack.mediaMetadata.extras?.apply {
                putInt(TRACK_KEY_FAVORITE, favorite)
            }
            _state.update { it.copy(favorite = favorite) }
        }
    }

    fun changeTrackFavorite(trackId: String) {
        val currentTrack = player?.currentMediaItem ?: return
        val id = currentTrack.mediaMetadata.extras?.getString(TRACK_KEY_ID) ?: return
        val favorite = currentTrack.mediaMetadata.extras?.getInt(TRACK_KEY_FAVORITE) ?: return

        if (id == trackId) {
            val favorite = if (favorite == 0) 1 else 0
            currentTrack.mediaMetadata.extras?.apply {
                putInt(TRACK_KEY_FAVORITE, favorite)
            }
            _state.update { it.copy(favorite = favorite) }
        }
    }

    private fun startProgressTracking() {
        if (progressTrackingJob?.isActive == true) return

        progressTrackingJob?.cancel()
        progressTrackingJob = serviceScope.launch(Dispatchers.Main) {
            while (player?.isPlaying == true) {
                val currentPosition = getCurrentPosition()
                val duration = getDuration().toFloat()
                if (duration <= 0) { // for radio
                    _trackProgress.update { TrackProgress(0f, TEXT_EMPTY) }
                } else {
                    val trackTime = (currentPosition / 1000).toTrackTime()
                    val progress = currentPosition / duration

                    _trackProgress.update { TrackProgress(progress, trackTime) }
                }
                delay(333)
            }
        }
    }

    private fun stopProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = null
    }

    private val playerListener = object : Player.Listener {
        override fun onPlayerErrorChanged(error: PlaybackException?) {
            when (error?.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT,
                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
                PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW,
                PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> {
                    if (player?.hasNextMediaItem() == true) {
                        player?.seekToNextMediaItem()
                        player?.prepare()
                        serviceScope.launch {
                            delay(500)
                            player?.play()
                        }
                    } else player?.stop()
                }
                //TODO handle other errors
                else -> println("!!! player error: ${error?.message}  code ${error?.errorCode}")
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            
            handlePlayerControlEvents(events, player)
            handleIsPlayingChanged(events, player)
            handleMetadataChanged(events, player)
            handleMediaItemTransaction(events)
        }


        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> println("!!! player state: STATE_IDLE")
                Player.STATE_BUFFERING -> println("!!! player state: STATE_BUFFERING")
                Player.STATE_READY -> println("!!! player state: STATE_READY")
                Player.STATE_ENDED -> println("!!! player state: STATE_ENDED")
            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)

            serviceScope.launch(Dispatchers.IO) {
                val playList = mutableListOf<ITrackShort>()

                for (i in 0 until timeline.windowCount) {
                    val mediaItem = timeline.getWindow(i, Timeline.Window()).mediaItem
                    playList.add(
                        TrackShort(
                            id = mediaItem.mediaId,
                            fileName = mediaItem.mediaMetadata.getFileName("Unknown track") //TODO to resources
                        )
                    )
                }

                _playList.update { playList }
            }
        }
    }

    private fun handleMediaItemTransaction(events: Player.Events) {
        if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
            updateNotification()
        }
    }

    private fun handleMetadataChanged(
        events: Player.Events,
        player: Player
    ) {
        if (events.contains(Player.EVENT_MEDIA_METADATA_CHANGED)) {
            updateTrackInfo(player.mediaMetadata)
        }
    }

    private fun handleIsPlayingChanged(
        events: Player.Events,
        player: Player
    ) {
        if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
            if (player.isPlaying) startProgressTracking() else stopProgressTracking()
        }
    }

    private fun handlePlayerControlEvents(
        events: Player.Events,
        player: Player
    ) {
        if (
            events.containsAny(
                Player.EVENT_IS_PLAYING_CHANGED,
                Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED,
                Player.EVENT_REPEAT_MODE_CHANGED,
                Player.EVENT_PLAYBACK_STATE_CHANGED
            )
        ) _state.update {
            it.copy(
                isPlaying = player.isPlaying,
                shuffle = player.shuffleModeEnabled,
                repeatMode = player.repeatMode
            )
        }
    }

    //TODO need add title and artist to metadata GLOBALY(flip after change track)
    private fun updateTrackInfo(mediaMetadata: MediaMetadata) {
        with(mediaMetadata) {

            val fileName = extras?.getString(TRACK_KEY_FILE_NAME, TEXT_EMPTY) ?: TEXT_EMPTY

            val trackArtist = artist.fixOldEncoding().ifNullOrEmpty { fileName }
            val trackTitle = title.fixOldEncoding().ifNullOrEmpty { fileName }
            val trackId = extras?.getString(TRACK_KEY_ID, UNKNOWN_TRACK_ID) ?: UNKNOWN_TRACK_ID
            val favorite = extras?.getInt(TRACK_KEY_FAVORITE, -1) ?: -1


            _state.update {
                it.copy(
                    id = trackId,
                    fileName = fileName,
                    title = trackTitle.orDefault(getString(R.string.feature_player_no_track)),
                    artist = trackArtist.orDefault(getString(R.string.feature_player_no_artist)),
                    favorite = favorite
                )
            }
        }
    }
}