package by.niaprauski.library.view

import androidx.compose.foundation.basicMarquee
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.button.TwoActionIconButton
import by.niaprauski.designsystem.ui.modifier.pulseAlphaBackgroundWithBottomDivider
import by.niaprauski.designsystem.ui.text.TextMedium
import by.niaprauski.library.models.LAction
import by.niaprauski.library.models.TrackModel
import by.niaprauski.translations.R


@Composable
fun TrackItem(
    track: TrackModel,
    onAction: (LAction) -> Unit,
    currentTrackId: () -> String
) {

    val textColor = AppTheme.appColors.text
    val textColorLight = AppTheme.appColors.text_ligth

    val contentColor = remember(track.isIgnore, textColor, textColorLight) {
        if (track.isIgnore) textColorLight else textColor
    }

    val isSelected = remember(track.id) {
        derivedStateOf { currentTrackId() == track.id }
    }.value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pulseAlphaBackgroundWithBottomDivider(isSelected, textColor)
            .wrapContentHeight()
            .clickable { onAction(LAction.PlayTrack(track)) }
            .padding(horizontal = AppTheme.padding.default, vertical = AppTheme.padding.default),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column(modifier = Modifier.weight(1f)) {
            TextMedium(
                modifier = Modifier.basicMarquee(
                    iterations = if (isSelected) Int.MAX_VALUE else 1,
                    initialDelayMillis = 3000,
                    velocity = 30.dp,
                ),
                text = track.nameWithFavorite,
                color = contentColor,
                maxLines = 1,
            )
        }

        TwoActionIconButton(
            modifier = Modifier.size(AppTheme.viewSize.small),
            model = track,
            isFirstAction = track.isIgnore,
            onActionFirstClick = { onAction(LAction.RestoreTrack(track)) },
            onActionSecondClick = { onAction(LAction.IgnoreTrack(track)) },
            iconFirst = remember { IIcon.reply },
            iconSecond = remember { IIcon.hide },
            descriptionFirst = stringResource(R.string.feature_library_restore_track),
            descriptionSecond = stringResource(R.string.feature_library_ignore),
        )
    }
}
