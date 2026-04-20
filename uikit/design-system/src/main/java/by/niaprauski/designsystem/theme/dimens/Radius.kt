package by.niaprauski.designsystem.theme.dimens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Radius(
    val zero: Dp,
    val micro: Dp,
    val mini: Dp,
    val default: Dp,
    val medium: Dp,
)

val defaultRadius = Radius(
    zero = 0.dp,
    micro = 2.dp,
    mini = 4.dp,
    default = 8.dp,
    medium = 16.dp,
)

val smallRadius = Radius(
    zero = 0.dp,
    micro = 2.dp,
    mini = 4.dp,
    default = 6.dp,
    medium = 8.dp,
)