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
import by.niaprauski.designsystem.ui.row.TextFieldRow
import by.niaprauski.translations.R

@Composable
fun SyncSettingView(
    minDuration: String,
    maxDuration: String,
    isMinDurationError: Boolean,
    isMaxDurationError: Boolean,
    onMinDurationChanged: (String) -> Unit,
    onMaxDurationChanged: (String) -> Unit
) {

    TextFieldRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(AppTheme.padding.mini),
        text = minDuration,
        label = stringResource(R.string.feature_settings_min_duration_sec),
        onValueChange = onMinDurationChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isMinDurationError,
    )

    TextFieldRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(AppTheme.padding.mini),
        text = maxDuration,
        label = stringResource(R.string.feature_settings_max_duration_min),
        onValueChange = onMaxDurationChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isMaxDurationError,
    )
}