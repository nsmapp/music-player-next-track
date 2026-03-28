package by.niaprauski.designsystem.extensions

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput


fun Modifier.onSwipe(
    onUp: () -> Unit = {},
    onDown: () -> Unit = {}
): Modifier = pointerInput(Unit) {
    var swipe = 0f
    val triggerDistance = 300f

    detectDragGestures(
        onDrag = { change, dragAmount ->
            change.consume()
            swipe += dragAmount.y
        },
        onDragEnd = {
            if (swipe < -triggerDistance) onUp()
            if (swipe > triggerDistance) onDown()
            swipe = 0f
        },
        onDragCancel = {
            swipe = 0f
        },
    )
}