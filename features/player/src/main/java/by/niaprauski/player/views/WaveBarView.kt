package by.niaprauski.player.views

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.playerservice.models.WaveformData
import kotlinx.coroutines.flow.StateFlow

@OptIn(UnstableApi::class)
@Composable
fun WaveBarView(
    modifier: Modifier = Modifier,
    waveformFlow: StateFlow<WaveformData>,
    isPlaying: Boolean,
) {
    if (!isPlaying) return

    val waveformData by waveformFlow.collectAsStateWithLifecycle()
    val barColor = AppTheme.appColors.text_ligth

    Spacer(
        modifier = modifier.drawWithCache {
            val wave = waveformData.values
            val count = wave.size
            val gapPx = 2.dp.toPx()
            val minHeightPx = 1.dp.toPx()
            val width = size.width
            val height = size.height

            val totalGapsWidth = (count - 1) * gapPx
            val totalBarWidth = width - totalGapsWidth
            val barWidth = if (count > 0 && totalBarWidth > 0f) totalBarWidth / count else 0f
            val cornerRadiusPx = barWidth / 2f
            val cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)

            onDrawBehind {
                if (count == 0 || barWidth <= 0f) return@onDrawBehind

                for (i in 0 until count) {
                    val value = wave[i]
                    val barHeight = (height * value).coerceIn(minHeightPx, height)
                    val x = i * (barWidth + gapPx)
                    val y = height - barHeight

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x = x, y = y),
                        size = Size(width = barWidth, height = barHeight),
                        cornerRadius = cornerRadius
                    )
                }
            }
        }
    )
}