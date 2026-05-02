package by.niaprauski.settings.models

import androidx.compose.runtime.Stable
import by.niaprauski.utils.constants.TEXT_EMPTY

@Stable
data class SettingsState(
    val isVisuallyEnabled: Boolean,
    val minDuration: String,
    val isMinDurationError: Boolean,
    val maxDuration: String,
    val isMaxDurationError: Boolean,
    val acentPositon: Float,
    val backgroundPosition: Float,
    val isPlayListLimitError: Boolean,
    val playListLimitSize: String,
    val isLikeTrackPriority: Boolean,
    val likedTrackPercent: String,
    val isAutoPlay: Boolean,
){

    companion object{
        val INITIAL = SettingsState(
            isVisuallyEnabled = false,
            minDuration = TEXT_EMPTY,
            isMinDurationError = false,
            maxDuration = TEXT_EMPTY,
            isMaxDurationError = false,
            acentPositon = 0f,
            backgroundPosition = 0f,
            isPlayListLimitError = false,
            playListLimitSize = "100",
            isLikeTrackPriority = true,
            likedTrackPercent = "50",
            isAutoPlay = false
        )
    }
}
