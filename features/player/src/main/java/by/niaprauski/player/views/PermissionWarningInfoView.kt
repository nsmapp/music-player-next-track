package by.niaprauski.player.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.dimens.defaultRoundedShape
import by.niaprauski.designsystem.ui.text.TextBoldLarge
import by.niaprauski.designsystem.ui.text.TextMedium
import by.niaprauski.player.models.PAction
import by.niaprauski.translations.R

@Composable
fun PermissionWarningInfoView(
    onAction: (PAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = AppTheme.padding.default)
            .background(
                AppTheme.appColors.background_hard,
                defaultRoundedShape
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextMedium(
            modifier = Modifier
                .weight(1f)
                .padding(AppTheme.padding.default),
            text = stringResource(R.string.feature_player_not_music_access), maxLines = 2
        )
        TextBoldLarge(
            modifier = Modifier
                .wrapContentSize()
                .padding(AppTheme.padding.default)
                .clip(defaultRoundedShape)
                .clickable {
                    onAction(PAction.RequestSync)
                },
            text = stringResource(R.string.feature_player_get)
        )
    }
}