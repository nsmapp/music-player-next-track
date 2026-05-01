package by.niaprauski.designsystem.ui.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.utils.extension.toHexString
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RainbowSlider(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    trackProgress: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onAccentColorChanged: (String, Float) -> Unit,
) {

    val sliderState = remember {
        SliderState(
            value = trackProgress,
            steps = steps,
            valueRange = valueRange
        )
    }

    sliderState.value = trackProgress

    LaunchedEffect(sliderState, colors) {
        snapshotFlow { sliderState.value }
            .map { value ->
                val point =
                    (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
                getColorAtFraction(colors, point)
            }
            .distinctUntilChanged()
            .collect { color ->
                onAccentColorChanged(color.toHexString(), sliderState.value)
            }
    }

    Slider(
        value = trackProgress,
        onValueChange = { newPosition ->
            sliderState.value = newPosition
        },
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        thumb = {
            Column(
                modifier = Modifier
                    .height(AppTheme.viewSize.small)
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .size(AppTheme.viewSize.micro)
                        .clip(RoundedCornerShape(AppTheme.viewSize.micro))
                        .background(AppTheme.appColors.text)
                        .background(AppTheme.appColors.accent)
                )
            }
        },
        track = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.viewSize.border_big)
                    .background(
                        brush = Brush.horizontalGradient(colors = colors),
                        shape = RoundedCornerShape(AppTheme.radius.micro)
                    )

            )
        }
    )
}

fun getColorAtFraction(colors: List<Color>, fraction: Float): Color {
    if (colors.isEmpty()) return Color.White
    if (colors.size == 1) return colors.first()

    val position = fraction.coerceIn(0f, 1f)
    val colorStop = 1f / (colors.size - 1)
    val startIndex = (position / colorStop).toInt().coerceAtMost(colors.size - 2)
    val endIndex = startIndex + 1
    val localFraction = (position - startIndex * colorStop) / colorStop

    return lerp(colors[startIndex], colors[endIndex], localFraction)
}

@Stable
val rainbowBackgroundColors = listOf(
    Color(0xFF75b18b), //0A1A14
    Color(0xFF004D40),
    Color(0xFF1A237E),
    Color(0xFF1A1515),
    Color(0xFFD9602F),
    Color(0xFFC5C552),
    Color(0xFFE5E5E1),
)

val rainbowTextColor = listOf(
    Color(0xFFE5E5E1),
    Color(0xFF212121),
    Color(0xFF657B83),
    Color(0xFFC5CAE9),
    Color(0xFF00FFD1),
    Color(0xFF33691E),
    Color(0xFFFFD700),
    Color(0xFFFF9100),
    Color(0xFF6B093E),
)