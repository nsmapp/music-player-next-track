package by.niaprauski.designsystem.ui.flipper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import by.niaprauski.designsystem.theme.AppTheme
import by.niaprauski.designsystem.ui.icons.SmallIcon
import by.niaprauski.designsystem.ui.text.TextBoldLarge

@Composable
fun FlipperView(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit,
) {

    var opened by rememberSaveable { mutableStateOf(true) }

    val dividerColor = AppTheme.appColors.accent
    val dividerHeight = AppTheme.viewSize.border_normal

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .semantics { role = Role.Button}
                .clickable { opened = !opened }
                .drawBehind {
                    drawLine(
                        color = dividerColor,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = dividerHeight.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
                .padding(AppTheme.padding.mini),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextBoldLarge(
                modifier = Modifier.weight(1f),
                text = title
            )

            //TODO replace to more funny icon
            SmallIcon(
                modifier = Modifier,
                imageVector = if (opened) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
            )
        }

        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth(),
            visible = opened,
            enter = expandVertically(),
            exit = shrinkVertically(),
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .drawBehind {
                            drawLine(
                                color = dividerColor,
                                start = Offset(size.width, 0f),
                                end = Offset(size.width, size.height),
                                strokeWidth = dividerHeight.toPx(),
                                cap = StrokeCap.Round,
                            )
                        }
                ) {
                    content()
                }

            }
        )

    }

}
