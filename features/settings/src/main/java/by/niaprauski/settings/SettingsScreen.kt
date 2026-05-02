package by.niaprauski.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.dimens.defaultRoundedShape
import by.niaprauski.designsystem.ui.flipper.FlipperView
import by.niaprauski.designsystem.ui.text.TextBoldLarge
import by.niaprauski.settings.models.SAction
import by.niaprauski.settings.models.SettingsState
import by.niaprauski.settings.view.PlayListSettingsView
import by.niaprauski.settings.view.SyncSettingView
import by.niaprauski.settings.view.UISettingsView
import by.niaprauski.translations.R


@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.onLaunch()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()


    SettingsScreenContent(
        state = state,
        onNavigateToAbout = onNavigateToAbout,
        onAction = viewModel::onAction
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsState,
    onNavigateToAbout: () -> Unit = {},
    onAction: (SAction) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = AppTheme.appColors.background)
            .statusBarsPadding()
            .padding(AppTheme.padding.default)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {

        FlipperView(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = AppTheme.padding.default),
            title = stringResource(R.string.feature_settings_playback)
        ) {

            PlayListSettingsView(
                playlistLimitSize = state.playListLimitSize,
                likedTrackPercent = state.likedTrackPercent,
                isPlayListLimitError = state.isPlayListLimitError,
                isLikeTrackPriority = state.isLikeTrackPriority,
                isAutoPlayAfterLaunch = state.isAutoPlay,
                onLimitTrackChanged = { count -> onAction(SAction.SetPlayListLimitSize(count)) },
                onLikedTrackChanged = { percent -> onAction(SAction.SetLikedTrackPercent(percent)) },
                onAutoPlayChanged = { enabled -> onAction(SAction.SetAutoPlay(enabled)) },
                onAddLikeTrackInPlayList = { isLikeTrackPriority ->
                    onAction(
                        SAction.SetLikeTrackPriority(
                            isLikeTrackPriority
                        )
                    )
                },
            )
        }

        FlipperView(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = AppTheme.padding.default),
            title = stringResource(R.string.feature_settings_interface)
        ) {
            UISettingsView(
                accentPosition = state.acentPositon,
                backgroundPosition = state.backgroundPosition,
                isVisuallyEnabled = state.isVisuallyEnabled,
                onAccentColorChanged = { hex, pos -> onAction(SAction.SetAccentColor(hex, pos)) },
                onBackgroundColorChanged = { hex, pos ->
                    onAction(
                        SAction.SetBackgroundColor(
                            hex,
                            pos
                        )
                    )
                },
                onVisuallyChanged = { enabled -> onAction(SAction.SetVisuallyEnabled(enabled)) }
            )
        }

        FlipperView(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = AppTheme.padding.default),
            title = stringResource(R.string.feature_settings_sync_settings)
        ) {
            SyncSettingView(
                minDuration = state.minDuration,
                maxDuration = state.maxDuration,
                isMinDurationError = state.isMinDurationError,
                isMaxDurationError = state.isMaxDurationError,
                onMinDurationChanged = { duration -> onAction(SAction.SetMinDuration(duration)) },
                onMaxDurationChanged = { duration -> onAction(SAction.SetMaxDuration(duration)) },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextBoldLarge(
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = AppTheme.padding.default)
                .clip(defaultRoundedShape)
                .clickable {
                    onNavigateToAbout()
                },
            text = stringResource(R.string.feature_settings_about_app)
        )

    }
}
