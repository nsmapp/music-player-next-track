package by.niaprauski.designsystem.ui.row

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.dimens.defaultRoundedShape
import by.niaprauski.designsystem.ui.text.TextMedium

@Composable
fun TextFieldRow(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
) {
    Row(
        modifier = modifier.defaultMinSize(minHeight = AppTheme.viewSize.normal),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextMedium(text = label)

        CompositionLocalProvider(
            LocalTextSelectionColors provides CTextSelectionColors.colors()
        ) {
            BasicTextField(
                modifier = Modifier
                    .width(AppTheme.viewSize.short_text_field)
                    .border(
                        border = BorderStroke(
                            width = AppTheme.viewSize.border_normal,
                            color = if (isError) AppTheme.appColors.warning else AppTheme.appColors.accent
                        ),
                        shape = defaultRoundedShape,
                    )
                    .padding(
                        horizontal = AppTheme.padding.default,
                        vertical = AppTheme.padding.mini
                    ),
                value = text,
                onValueChange = { text -> onValueChange(text) },
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = true,
                maxLines = 1,
                textStyle = AppTheme.typography.medium.copy(
                    color = AppTheme.appColors.text,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(AppTheme.appColors.accent)

            )
        }
    }
}

private object CTextSelectionColors {
    @Composable
    fun colors(): TextSelectionColors {
        val handleColor = AppTheme.appColors.accent
        val backgroundColor = AppTheme.appColors.foreground

        return remember(handleColor, backgroundColor) {
            TextSelectionColors(
                handleColor = handleColor,
                backgroundColor = backgroundColor,
            )
        }
    }
}