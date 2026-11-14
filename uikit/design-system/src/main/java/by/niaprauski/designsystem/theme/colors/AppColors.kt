package by.niaprauski.designsystem.theme.colors

import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import by.niaprauski.designsystem.theme.AppTheme

@Stable
data class DayColors(
    override val background: Color = Color(0xFF75B1A9),
    override val background_hard: Color = Color(0xFF589791),
    override val background_hard_065: Color = Color(0xA6589791),
    override val foreground: Color = Color(0xFF75b18b),
    override val foreground_light: Color = Color(0xFF7FBF96),
    override val text: Color = Color(0xFFE5E5E0),
    override val text_ligth: Color = Color(0x59F4F4EF),
    override val accent: Color = Color(0xFFE5E5E1),
    override val accent20: Color = Color(0x33E5E5E1),
    override val transparent: Color =  Color(0x00FFFFFF),
    override val warning: Color = Color(0xFFed5752),
): AppColors

@Stable
interface AppColors{
    val background: Color
    val background_hard: Color
    val background_hard_065: Color
    val foreground: Color
    val foreground_light: Color
    val text: Color
    val text_ligth: Color
    val accent: Color
    val accent20: Color
    val transparent: Color
    val warning: Color
}

val navigationBarItemColors: NavigationBarItemColors
    @Composable
    get() = NavigationBarItemDefaults.colors().copy(selectedIndicatorColor = AppTheme.appColors.background)


@Stable
val dayColorScheme = lightColorScheme(
//    primary = defBackground,
//    primaryContainer = defBackground,
//    onSurface = defBackground,
//    background = defBackground
)

