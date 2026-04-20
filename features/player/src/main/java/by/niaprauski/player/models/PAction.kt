package by.niaprauski.player.models

sealed class PAction {
    object Play: PAction()
    object Pause: PAction()
    object Stop: PAction()
    object PlayNext: PAction()
    object PlayPrevious: PAction()
    object ChangeShuffleMode: PAction()
    object ChangeRepeatMode: PAction()
    object RequestSync: PAction()
    object ShowPermissionInformationDialog: PAction()
    object HideWelcomeDialogs: PAction()
    object HideMediaPermissionInfoDialog: PAction()
    object ShowPlayList: PAction()
    object HidePlayList: PAction()
    object ReloadPlayList: PAction()
    data class SeekTo(val position: Float): PAction()
    data class UpTrackFavorite(val trackId: String): PAction()
    data class ChangeTrackFavorite(val trackId: String): PAction()
    data class RemoveTrackFromPlayList(val trackId: String): PAction()
    data class PlayTrackFromPlayList(val trackId: String): PAction()

    data class SyncTracks(val status: SyncTrackStatus): PAction()
}