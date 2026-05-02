package by.niaprauski.settings.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.ui.row.SwitchRow
import by.niaprauski.designsystem.ui.row.TextFieldRow
import by.niaprauski.translations.R

@Composable
fun PlayListSettingsView(
    playlistLimitSize: String,
    likedTrackPercent: String,
    isPlayListLimitError: Boolean,
    isLikeTrackPriority: Boolean,
    isAutoPlayAfterLaunch: Boolean,
    onLimitTrackChanged: (String) -> Unit,
    onLikedTrackChanged: (String) -> Unit,
    onAutoPlayChanged: (Boolean) -> Unit,
    onAddLikeTrackInPlayList: (Boolean) -> Unit,
) {

    SwitchRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(AppTheme.padding.mini),
        isChecked = isAutoPlayAfterLaunch,
        label = stringResource(R.string.feature_settings_autoplay_after_launch),
        onCheckedChange = onAutoPlayChanged,
    )

    TextFieldRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(AppTheme.padding.mini),
        text = playlistLimitSize,
        label = stringResource(R.string.feature_settings_max_tracks),
        onValueChange = onLimitTrackChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isPlayListLimitError,
    )

    TextFieldRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(AppTheme.padding.mini),
        text = likedTrackPercent,
        label = stringResource(R.string.feature_settings_liked_track_in_playlist),
        onValueChange = onLikedTrackChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isPlayListLimitError,
    )

    SwitchRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(AppTheme.padding.mini),
        isChecked = isLikeTrackPriority,
        label = stringResource(R.string.feature_settings_liked_priority),
        onCheckedChange = onAddLikeTrackInPlayList,
    )
}