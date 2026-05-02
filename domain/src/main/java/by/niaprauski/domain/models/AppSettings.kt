package by.niaprauski.domain.models

data class AppSettings(
    val isShowWelcomeMessage: Boolean,
    val isVisuallyEnabled: Boolean,
    val minDuration: Int,
    val maxDuration: Int,
    val accentColorHex: String,
    val backgroundColorHex: String,
    val accentPosition: Float,
    val backgroundPosition: Float,
    val playListLimitSize: Int,
    val isLikeTrackPriority: Boolean,
    val likedTrackPercent: Int,
    val isAutoPlayOnLaunch: Boolean,
)