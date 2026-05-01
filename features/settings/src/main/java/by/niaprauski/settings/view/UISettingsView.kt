package by.niaprauski.settings.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.ui.row.SwitchRow
import by.niaprauski.designsystem.ui.slider.RainbowSlider
import by.niaprauski.designsystem.ui.slider.rainbowBackgroundColors
import by.niaprauski.designsystem.ui.slider.rainbowTextColor
import by.niaprauski.designsystem.ui.text.TextMedium
import by.niaprauski.translations.R

@Composable
fun UISettingsView(
    accentPosition: Float,
    backgroundPosition: Float,
    isVisuallyEnabled: Boolean,
    onAccentColorChanged: (String, Float) -> Unit,
    onBackgroundColorChanged: (String, Float) -> Unit,
    onVisuallyChanged: (Boolean) -> Unit
) {

    TextMedium(
        modifier = Modifier.padding(top = AppTheme.padding.default),
        text = stringResource(R.string.feature_settings_text_color)
    )

    RainbowSlider(
        trackProgress = accentPosition,
        valueRange = 0f..1f,
        steps = 0,
        colors = rainbowTextColor,
        onAccentColorChanged = onAccentColorChanged
    )

    TextMedium(
        modifier = Modifier.padding(top = AppTheme.padding.default),
        text = stringResource(R.string.feature_settings_background_color)
    )

    RainbowSlider(
        trackProgress = backgroundPosition,
        valueRange = 0f..1f,
        steps = 0,
        colors = rainbowBackgroundColors,
        onAccentColorChanged = onBackgroundColorChanged
    )

    SwitchRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(AppTheme.padding.mini),
        isChecked = isVisuallyEnabled,
        label = stringResource(R.string.feature_settings_visually),
        onCheckedChange = onVisuallyChanged,
    )
}