package by.niaprauski.playerservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import by.niaprauski.playerservice.models.ExoPlayerState
import by.niaprauski.playerservice.models.TrackProgress
import by.niaprauski.playerservice.models.WaveformData
import by.niaprauski.utils.media.ITrackShort
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(UnstableApi::class, ExperimentalCoroutinesApi::class)
@Singleton
class PlayerConnectionController @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceConnection {

    private val _service = MutableStateFlow<PlayerService?>(null)
    val service: StateFlow<PlayerService?> = _service.asStateFlow()

    private var isBound = false

    val exoPlayerState: Flow<ExoPlayerState> = _service.flatMapLatest { playerService ->
        playerService?.state ?: flowOf(ExoPlayerState.DEFAULT)
    }

    val playList: Flow<List<ITrackShort>> = _service.flatMapLatest { playerService ->
        playerService?.playList ?: flowOf(emptyList())
    }

    val trackProgress: Flow<TrackProgress> = _service.flatMapLatest { playerService ->
        playerService?.trackProgress ?: flowOf(TrackProgress.DEFAULT)
    }

    val waveform: Flow<WaveformData> = _service.flatMapLatest { playerService ->
        playerService?.waveform ?: flowOf(WaveformData.DEFAULT)
    }

    init {
        bind()
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        (binder as? PlayerService.PlayerBinder)?.getService()?.let { playerService ->
            _service.value = playerService
        }
        isBound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        _service.value = null
        isBound = false
    }

    fun bind() {
        if (!isBound) {
            val intent = Intent(context, PlayerService::class.java)
            context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbind() {
        if (isBound) {
            context.unbindService(this)
            isBound = false
            _service.value = null
        }
    }
}
