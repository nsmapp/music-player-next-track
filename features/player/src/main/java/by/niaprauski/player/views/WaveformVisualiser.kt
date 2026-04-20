package by.niaprauski.player.views

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaveformVisualizer(
    waveform: FloatArray,
    modifier: Modifier = Modifier,
    barColor: Color,
    barGap: Dp = 2.dp,
    minBarHeight: Dp = 1.dp
) {
    Canvas(modifier = modifier) {
        if (waveform.isEmpty()) return@Canvas

        val gapPx = barGap.toPx()
        val minHeightPx = minBarHeight.toPx()
        val count = waveform.size
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = (canvasWidth - (count - 1) * gapPx) / count

        if (barWidth <= 0f) return@Canvas

        for (index in waveform.indices) {
            val value = waveform[index]
            val barHeight = (canvasHeight * value)
                .coerceIn(minHeightPx, canvasHeight)
            val x = index * (barWidth + gapPx)
            val y = (canvasHeight - barHeight)

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}