package by.niaprauski.player.models

import androidx.media3.common.MediaItem

sealed class PlayerEvent {

    object Play: PlayerEvent()
    object PlayNext: PlayerEvent()
    object PlayPrevious: PlayerEvent()
    object Stop: PlayerEvent()
    object Pause: PlayerEvent()

    data class SeekTo(val position: Float): PlayerEvent()

    object ChangeShuffleMode: PlayerEvent()
    object ChangeRepeatMode: PlayerEvent()


    data class SetPlayList(val tracks: List<MediaItem>): PlayerEvent()
    data class PlaySingleTrack(val track: MediaItem): PlayerEvent()
    object SyncPlayList: PlayerEvent()

    data class UpFavorite(val trackId: String): PlayerEvent()
    data class ChangeFavorite(val trackId: String): PlayerEvent()

    data class RemoveTrackFromPlayList(val trackId: String): PlayerEvent()

    data class PlayTrackFromPlayList(val trackId: String): PlayerEvent()

    object MediaItemSynced: PlayerEvent()
    object MediaItemSyncError: PlayerEvent()

    object Nothing: PlayerEvent()
}