package by.niaprauski.library.view

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.dimens.defaultRoundedShape
import by.niaprauski.designsystem.theme.icons.IIcon
import by.niaprauski.designsystem.ui.button.TwoActionIconButton
import by.niaprauski.designsystem.ui.text.TextMedium
import by.niaprauski.library.models.LAction
import by.niaprauski.translations.R

@Composable
fun SimplePlayer(
    currentTrackName: () -> String,
    isPlaying: () -> Boolean,
    onAction: (LAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(AppTheme.appColors.background_hard, defaultRoundedShape)
            .padding(horizontal = AppTheme.padding.default)
            .fillMaxWidth()
            .height(AppTheme.viewSize.big),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextMedium(
            modifier = Modifier.weight(1f)
                .basicMarquee(),
            text = currentTrackName(),
            color = AppTheme.appColors.text,
            maxLines = 1,
        )

        TwoActionIconButton(
            modifier = Modifier.size(AppTheme.viewSize.normal),
            model = Unit,
            isFirstAction = isPlaying(),
            onActionFirstClick = {onAction(LAction.Pause)},
            onActionSecondClick = {onAction(LAction.Play)},
            iconFirst = remember { IIcon.pause},
            iconSecond = remember { IIcon.play },
            descriptionFirst = stringResource(R.string.feature_library_pause),
            descriptionSecond = stringResource(R.string.feature_player_play),
        )
    }
}