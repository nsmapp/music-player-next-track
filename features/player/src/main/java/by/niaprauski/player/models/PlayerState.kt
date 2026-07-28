package by.niaprauski.player.models

import by.niaprauski.playerservice.models.ExoPlayerState
import by.niaprauski.utils.media.ITrackShort

data class PlayerState(
    val isShowWelcomeDialog: Boolean,
    val isShowPermissionInformationDialog: Boolean,
    val trackCount: Int,
    val isVisuallyEnabled: Boolean,
    val isSyncing: Boolean,
    val isShowPlayList: Boolean,
    val exoPlayerState: ExoPlayerState,
    val playList: List<ITrackShort>,
) {
    companion object {

        val DEFAULT = PlayerState(
            isShowWelcomeDialog = false,
            isShowPermissionInformationDialog = false,
            trackCount = 0,
            isVisuallyEnabled = true,
            isSyncing = false,
            isShowPlayList = false,
            exoPlayerState = ExoPlayerState.DEFAULT,
            playList = emptyList(),
        )
    }
}