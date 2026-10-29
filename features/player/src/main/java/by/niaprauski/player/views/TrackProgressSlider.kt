package by.niaprauski.player.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.ui.slider.CSlider
import by.niaprauski.designsystem.ui.text.TextMediumSmall
import by.niaprauski.player.models.PAction
import by.niaprauski.playerservice.models.TrackProgress

@Composable
fun TrackProgressSlider(
    trackProgress: () -> TrackProgress,
    onAction: (PAction) -> Unit,
) {


    var sliderPosition by remember { mutableFloatStateOf(trackProgress.invoke().progress) }
    var isDragging by remember { mutableStateOf(false) }
    val timePosition by remember {
        derivedStateOf { trackProgress.invoke().currentPosition }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { trackProgress().progress }
            .collect { progress ->
                if (!isDragging) {
                    sliderPosition = progress
                }
            }
    }

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        CSlider(
            modifier = Modifier.fillMaxWidth(),
            progress = sliderPosition,
            valueRange = 0f..1f,
            steps = 0,
            onValueChange = { newPosition ->
                isDragging = true
                sliderPosition = newPosition
            },
            onValueChangeFinished = {
                isDragging = false
                onAction(PAction.SeekTo(sliderPosition))
            }
        )

        TextMediumSmall(
            modifier = Modifier.padding(end = AppTheme.padding.default),
            text = timePosition,
        )
    }

}