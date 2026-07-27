package by.niaprauski.utils.extension

fun Long.toTrackTime(fillDuration: Float): String {
    val currentMin = this / 60
    val currentSec = (this % 60).toString().padStart(2, '0')

    val total = (fillDuration / 1000).toLong()
    val totalMin = total / 60
    val totalSec = (total % 60).toString().padStart(2, '0')

    return "$currentMin:$currentSec/$totalMin:$totalSec"
}

const val UNKNOWN_TRACK_ID = "-1L"
