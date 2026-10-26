package by.niaprauski.player.views

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.button.PlayerLiteButton
import by.niaprauski.designsystem.ui.modifier.pulseAlphaBackgroundWithBottomDivider
import by.niaprauski.designsystem.ui.text.TextMedium
import by.niaprauski.player.models.PAction
import by.niaprauski.translations.R
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
            .pulseAlphaBackgroundWithBottomDivider(isSelected, textColor)
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
            imageVector = IIcon.cancel,
            onClick = handleRemoveClick,
            description = stringResource(R.string.feature_player_delete_from_playlist),
        )
    }
}
