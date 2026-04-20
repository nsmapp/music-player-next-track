package by.niaprauski.designsystem.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import by.niaprauski.designsystem.theme.colors.AppColors
import by.niaprauski.designsystem.theme.colors.DayColors
import by.niaprauski.designsystem.theme.colors.dayColorScheme
import by.niaprauski.designsystem.theme.dimens.Padding
import by.niaprauski.designsystem.theme.dimens.Radius
import by.niaprauski.designsystem.theme.dimens.ViewSize
import by.niaprauski.designsystem.theme.dimens.defaultPaddings
import by.niaprauski.designsystem.theme.dimens.defaultRadius
import by.niaprauski.designsystem.theme.dimens.defaultViewSizes
import by.niaprauski.designsystem.theme.dimens.smallPaddings
import by.niaprauski.designsystem.theme.dimens.smallRadius
import by.niaprauski.designsystem.theme.dimens.smallViewSizes
import by.niaprauski.designsystem.theme.typography.OpenSansTypography
import by.niaprauski.designsystem.theme.typography.opensansTypography
import by.niaprauski.utils.constants.TEXT_EMPTY
import by.niaprauski.utils.extension.darken
import by.niaprauski.utils.extension.lighten
import by.niaprauski.utils.extension.toColor
import kotlinx.coroutines.CoroutineScope

//TODO may be use material3 window?
private const val SMALL_SCREEN_WIDTH_DP = 360

@Composable
fun AppTheme(
    accentColor: String,
    backgroundColor: String,
    content: @Composable () -> Unit,
) {

    val configuration = LocalConfiguration.current
    val isSmallScreen = configuration.screenWidthDp <= SMALL_SCREEN_WIDTH_DP

    //TODO support small screen?
    val typography = opensansTypography
    val padding = if (isSmallScreen) smallPaddings else defaultPaddings
    val radius = if (isSmallScreen) smallRadius else defaultRadius
    val viewSize = if (isSmallScreen) smallViewSizes else defaultViewSizes


    val dayColors = remember(accentColor, backgroundColor) {

        val accentColor = accentColor.toColor()
        val backgroundColor = backgroundColor.toColor()

        DayColors().copy(
            accent = accentColor,
            foreground = backgroundColor,
            foreground_light = backgroundColor.lighten(0.2f),
            background = backgroundColor.darken(0.2f),
            background_hard = backgroundColor.darken(0.4f),
            text = accentColor,
            text_ligth = accentColor.copy(alpha = 0.35f),
        )
    }
    val colorScheme = dayColorScheme

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    SetSystemAppearance()

    CompositionLocalProvider(
        LocalTypography provides typography,
        LocalPadding provides padding,
        LocalRadius provides radius,
        LocalViewSize provides viewSize,
        LocalAppColors provides dayColors,
        LocalColorScheme provides colorScheme,
        LocalSnackBarHostState provides snackBarHostState,
        LocalCoroutineScope provides coroutineScope
    ) {
        MaterialTheme(
            content = content,
            colorScheme = colorScheme
        )
    }

}

val LocalTypography = staticCompositionLocalOf<OpenSansTypography> { error(TEXT_EMPTY) }
val LocalPadding = staticCompositionLocalOf<Padding> { error(TEXT_EMPTY) }
val LocalRadius = staticCompositionLocalOf<Radius> { error(TEXT_EMPTY) }
val LocalViewSize = staticCompositionLocalOf<ViewSize> { error(TEXT_EMPTY) }
val LocalAppColors = staticCompositionLocalOf<AppColors> { error(TEXT_EMPTY) }
val LocalColorScheme = staticCompositionLocalOf<ColorScheme> { error(TEXT_EMPTY) }
val LocalSnackBarHostState = staticCompositionLocalOf<SnackbarHostState> { error(TEXT_EMPTY) }
val LocalCoroutineScope = staticCompositionLocalOf<CoroutineScope> { error(TEXT_EMPTY) }


@Stable
object AppTheme {

    val typography: OpenSansTypography
        @Composable get() = LocalTypography.current

    val padding: Padding
        @Composable get() = LocalPadding.current

    val radius: Radius
        @Composable get() = LocalRadius.current

    val viewSize: ViewSize
        @Composable get() = LocalViewSize.current

    val appColors: AppColors
        @Composable get() = LocalAppColors.current

}

@Composable
private fun SetSystemAppearance() {
    val view = LocalView.current

    LaunchedEffect(Unit) {
        val window = (view.context as Activity).window

        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }
}