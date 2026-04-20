package by.niaprauski.designsystem.ui.texxtfield

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.theme.dimens.defaultRoundedShape
import by.niaprauski.designsystem.ui.icons.SmallIcon
import by.niaprauski.designsystem.ui.text.TextMedium

@Composable
fun CTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppTheme.typography.medium,
    hint: String? = null,
    leadingIcon: ImageVector? = null,
    onLeadingClick: () -> Unit = {},
    trailingIcon: ImageVector? = null,
    onTrailingClick: () -> Unit = {},
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    shape: Shape = defaultRoundedShape,
    colors: TextFieldColors = CTextFieldDefaults.colors()
) {

    TextField(
        modifier = modifier.border(
            border = BorderStroke(
                width = AppTheme.viewSize.border_normal,
                color = if (isError) AppTheme.appColors.warning else AppTheme.appColors.accent
            ), shape = shape
        ),
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        placeholder = {
            hint?.let {
                TextMedium(
                    text = hint, color = colors.focusedPlaceholderColor
                )
            }
        },
        leadingIcon = {
            leadingIcon?.let { icon ->
                SmallIcon(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onLeadingClick() }),
                    imageVector = icon,
                    colorFilter = ColorFilter.tint(AppTheme.appColors.text_ligth)
                )
            }
        },
        trailingIcon = {
            trailingIcon?.let { icon ->
                SmallIcon(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTrailingClick() }),
                    imageVector = icon,
                    colorFilter = ColorFilter.tint(AppTheme.appColors.text)
                )
            }
        },
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = shape,
        colors = colors,
    )

}

object CTextFieldDefaults {
    @Composable
    fun colors(): TextFieldColors {

        val text = AppTheme.appColors.text
        val textLigth = AppTheme.appColors.text_ligth
        val background = AppTheme.appColors.background
        val backgroundHard = AppTheme.appColors.background_hard
        val accent = AppTheme.appColors.accent
        val warning = AppTheme.appColors.warning
        val transparent = AppTheme.appColors.transparent
        val foreground = AppTheme.appColors.foreground

        return remember(
            text, textLigth, background, backgroundHard, accent, warning, transparent, foreground
        ) {
            TextFieldColors(
                focusedTextColor = text,
                unfocusedTextColor = text,
                disabledTextColor = textLigth,
                errorTextColor = accent,
                focusedContainerColor = backgroundHard,
                unfocusedContainerColor = background,
                disabledContainerColor = background,
                errorContainerColor = background,
                cursorColor = accent,
                errorCursorColor = warning,
                textSelectionColors = TextSelectionColors(
                    handleColor = accent, backgroundColor = foreground
                ),
                focusedIndicatorColor = transparent,
                unfocusedIndicatorColor = transparent,
                disabledIndicatorColor = transparent,
                errorIndicatorColor = transparent,
                focusedLeadingIconColor = textLigth,
                unfocusedLeadingIconColor = textLigth,
                disabledLeadingIconColor = textLigth,
                errorLeadingIconColor = textLigth,
                focusedTrailingIconColor = text,
                unfocusedTrailingIconColor = text,
                disabledTrailingIconColor = text,
                errorTrailingIconColor = text,
                focusedLabelColor = textLigth,
                unfocusedLabelColor = textLigth,
                disabledLabelColor = textLigth,
                errorLabelColor = warning,
                focusedPlaceholderColor = textLigth,
                unfocusedPlaceholderColor = textLigth,
                disabledPlaceholderColor = textLigth,
                errorPlaceholderColor = warning,
                focusedSupportingTextColor = textLigth,
                unfocusedSupportingTextColor = textLigth,
                disabledSupportingTextColor = textLigth,
                errorSupportingTextColor = warning,
                focusedPrefixColor = text,
                unfocusedPrefixColor = text,
                disabledPrefixColor = textLigth,
                errorPrefixColor = text,
                focusedSuffixColor = text,
                unfocusedSuffixColor = text,
                disabledSuffixColor = textLigth,
                errorSuffixColor = text,
            )
        }
    }
}
