package by.niaprauski.library.models

import by.niaprauski.domain.models.tag.Tag

sealed class LAction {
    object Play : LAction()
    object Pause : LAction()
    data class Search(val text: String) : LAction()
    data class PlayTrack(val track: TrackModel) : LAction()
    data class IgnoreTrack(val track: TrackModel) : LAction()
    data class RestoreTrack(val track: TrackModel) : LAction()
    data class ShowTracksByTag(val tag: Tag) : LAction()
    object PlayFiltered : LAction()

}