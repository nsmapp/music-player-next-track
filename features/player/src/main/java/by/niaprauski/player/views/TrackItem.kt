package by.niaprauski.player.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.button.PlayerLiteButton
import by.niaprauski.designsystem.ui.text.TextMedium
import by.niaprauski.player.models.PAction
import by.niaprauski.utils.media.ITrackShort


@Composable
fun TrackItem(
    track: ITrackShort,
    onAction: (PAction) -> Unit,
    currentTrackId: () -> String
) {

    val textColor = AppTheme.appColors.text
    val contentColor = remember(textColor) { textColor }
    val isSelected = remember(track.id) {
        derivedStateOf { currentTrackId() == track.id }
    }.value

    val onRemoveTrackClick by rememberUpdatedState(onAction)
    val onPlayTrackClick by rememberUpdatedState(onAction)
    val currentModel by rememberUpdatedState(track)

    val handleRemoveClick = remember { { onRemoveTrackClick(PAction.RemoveTrackFromPlayList(currentModel.id))} }
    val handlePlayClick = remember { { onPlayTrackClick(PAction.PlayTrackFromPlayList(currentModel.id)) } }



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectedBackground(isSelected, textColor)
            .wrapContentHeight()
            .clickable { handlePlayClick() }
            .padding(horizontal = AppTheme.padding.default, vertical = AppTheme.padding.default),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(modifier = Modifier.weight(1f)) {
            TextMedium(
                text = track.fileName,
                color = contentColor,
                maxLines = 1,
            )
        }

        PlayerLiteButton(
            modifier = Modifier.size(AppTheme.viewSize.small),
            imageVector = IIcon.remove,
            onClick = handleRemoveClick,
            description = "Выдаліць трэк з плэйліста",
        )
    }
}


private fun Modifier.selectedBackground(
    isSelected: Boolean,
    color: Color,
) = composed {
    val density = LocalDensity.current

    val pulseAlpha by if (isSelected) {
        val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
        infiniteTransition.animateFloat(
            initialValue = 0.03f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PulseAlpha"
        )
    } else remember { mutableFloatStateOf(0f) }

    val drawParams = remember(density) {
        with(density) {
            val radius = 4.dp.toPx()
            object {
                val dividerPx = 1.dp.toPx()
                val offsetPx = 8.dp.toPx()
                val cornerRadius = CornerRadius(radius, radius)
                val dividerColor = color.copy(alpha = 0.2f)
            }
        }
    }

    this.drawBehind {
        if (isSelected) {
            drawRoundRect(
                color = color.copy(alpha = pulseAlpha),
                size = size,
                cornerRadius = drawParams.cornerRadius
            )
        }

        val y = size.height - drawParams.dividerPx / 2
        drawLine(
            color = drawParams.dividerColor,
            start = Offset(drawParams.offsetPx, y),
            end = Offset(size.width - drawParams.offsetPx, y),
            strokeWidth = drawParams.dividerPx
        )
    }
}