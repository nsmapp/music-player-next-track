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
    override val background_dark_085: Color = Color(0xD90A1E05),
    override val foreground: Color = Color(0xFF75b18b),
    override val foreground_light: Color = Color(0xFF7FBF96),
    override val text: Color = Color(0xFFE5E5E0),
    override val text_ligth: Color = Color(0x59F4F4EF),
    override val accent: Color = Color(0xFFE5E5E1),
    override val transparent: Color =  Color(0x00FFFFFF),
    override val warning: Color = Color(0xFFed5752),
): AppColors

@Stable
data class NightColors(
    override val background: Color = Color(0xFF3B3D4C),
    override val background_hard: Color =  Color(0xFF2F2F3B),
    override val background_dark_085: Color = Color(0xD90A1E05),
    override val foreground: Color =  Color(0xff282a36),
    override val foreground_light: Color = Color(0xFF2D2F3A),
    override val text: Color = Color(0xFFE5E5E0),
    override val text_ligth: Color = Color(0x59F4F4EF),
    override val accent: Color = Color(0xFFE5E5E0),
    override val transparent: Color =  Color(0x00FFFFFF),
    override val warning: Color = Color(0xFFed5752),
): AppColors

@Stable
interface AppColors{
    val background: Color
    val background_hard: Color
    val background_dark_085: Color
    val foreground: Color
    val foreground_light: Color
    val text: Color
    val text_ligth: Color
    val accent: Color
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

