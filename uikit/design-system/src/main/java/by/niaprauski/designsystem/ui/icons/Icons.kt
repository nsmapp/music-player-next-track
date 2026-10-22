package by.niaprauski.designsystem.ui.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import by.niaprauski.designsystem.theme.AppTheme

@Composable
fun MicroIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    colorFilter: ColorFilter? = ColorFilter.tint(AppTheme.appColors.accent),
    contentDescription:String? = null,
){
    BaseIcon(
        modifier = modifier,
        imageVector = imageVector,
        size = AppTheme.viewSize.micro,
        contentDescription = contentDescription,
        colorFilter = colorFilter,
    )
}

@Composable
fun SmallIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    colorFilter: ColorFilter? = ColorFilter.tint(AppTheme.appColors.accent),
    contentDescription:String? = null,
){
    BaseIcon(
        modifier = modifier,
        imageVector = imageVector,
        size = AppTheme.viewSize.small,
        contentDescription = contentDescription,
        colorFilter = colorFilter,
    )
}

@Composable
fun NormalIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    colorFilter: ColorFilter? = ColorFilter.tint(AppTheme.appColors.accent),
    contentDescription: String? = null,
){
    BaseIcon(
        modifier = modifier,
        imageVector = imageVector,
        size = AppTheme.viewSize.normal,
        contentDescription = contentDescription,
        colorFilter = colorFilter,
    )
}