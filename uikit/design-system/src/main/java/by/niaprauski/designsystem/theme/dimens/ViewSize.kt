package by.niaprauski.designsystem.theme.dimens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Immutable
data class ViewSize(
    val micro: Dp,
    val small: Dp,
    val medium_rc: Dp,
    val normal: Dp,
    val big:Dp,
    val large:Dp,
    val extra_large:Dp,

    val border_zero: Dp,
    val border_small: Dp,
    val border_normal: Dp,
    val border_big: Dp,

    val view_extra_larger: Dp,
    val short_text_field: Dp,

    )

val defaultViewSizes = ViewSize(
    micro = 12.dp,
    small = 24.dp,
    medium_rc = 32.dp,
    normal = 48.dp,
    big = 64.dp,
    large = 96.dp,
    extra_large = 160.dp,

    border_zero = 0.dp,
    border_small = 0.75.dp,
    border_normal = 2.dp,
    border_big = 4.dp,

    view_extra_larger = 192.dp,
    short_text_field = 64.dp,
)