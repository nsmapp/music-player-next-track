package by.niaprauski.designsystem.ui.modifier

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.pulseAlphaBackgroundWithBottomDivider(
    isSelected: Boolean,
    color: Color,
): Modifier {

    val pulseAlpha: () -> Float = if (isSelected) {
        val infiniteTransition = rememberInfiniteTransition()
        val pulseAlphaState = infiniteTransition.animateFloat(
            initialValue = 0.03f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
        )
        val alpha: () -> Float = { pulseAlphaState.value }
        alpha
    } else {
        { 0f }
    }

    return this.drawWithCache {
        val radius = 4.dp.toPx()
        val dividerPx = 1.dp.toPx()
        val offsetPx = 8.dp.toPx()
        val cornerRadius = CornerRadius(radius, radius)
        val dividerColor = color.copy(alpha = 0.2f)

        onDrawBehind {
            if (isSelected) {
                drawRoundRect(
                    color = color.copy(alpha = pulseAlpha()),
                    size = size,
                    cornerRadius = cornerRadius
                )
            }

            val y = size.height - dividerPx / 2
            drawLine(
                color = dividerColor,
                start = Offset(offsetPx, y),
                end = Offset(size.width - offsetPx, y),
                strokeWidth = dividerPx
            )
        }
    }
}