package by.niaprauski.player.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.playerservice.models.WaveformData
import kotlinx.coroutines.FlowPreview

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn( FlowPreview::class)
@Composable
fun WaveBarView(
    modifier: Modifier,
    waveform: WaveformData,
    isPlaying: Boolean,
) {

    if (isPlaying && waveform.values.isNotEmpty()) {
        WaveformVisualizer(
            modifier = modifier,
            waveform = waveform.values,
            barColor = AppTheme.appColors.text_ligth
        )
    }
}