package by.niaprauski.settings.models

sealed class SAction {
    data class SetVisuallyEnabled(val enabled: Boolean) : SAction()
    data class SetMinDuration(val duration: String) : SAction()
    data class SetMaxDuration(val duration: String) : SAction()
    data class SetAccentColor(val hexColor: String, val position: Float) : SAction()
    data class SetBackgroundColor(val hexColor: String, val position: Float) : SAction()
    data class SetPlayListLimitSize(val count: String) : SAction()

    data class SetLikeTrackPriority(val isLikeTrackPriority: Boolean) : SAction()
    data class SetLikedTrackPercent(val percent: String): SAction()
    data class SetAutoPlay(val enabled: Boolean) : SAction()
}