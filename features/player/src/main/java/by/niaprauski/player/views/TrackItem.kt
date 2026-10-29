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
import androidx.compose.runtime.remember
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
    isSelected: Boolean,
) {

    val textColor = AppTheme.appColors.text
    val contentColor = remember(textColor) { textColor }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pulseAlphaBackgroundWithBottomDivider(
                isSelected = isSelected,
                color = textColor
            )
            .wrapContentHeight()
            .clickable { onAction(PAction.PlayTrackFromPlayList(track.id)) }
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
            onClick = { onAction(PAction.RemoveTrackFromPlayList(track.id)) },
            description = stringResource(R.string.feature_player_delete_from_playlist),
        )
    }
}
