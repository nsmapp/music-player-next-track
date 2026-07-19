package by.niaprauski.library.mapper

import androidx.media3.common.MediaItem
import by.niaprauski.domain.models.track.Track
import by.niaprauski.library.models.TrackModel
import by.niaprauski.utils.media.MediaHandler
import javax.inject.Inject

class TrackModelMapper @Inject constructor() {

    fun toTrackModel(track: Track): TrackModel =
        TrackModel(
            id = track.id,
            fileName = track.fileName,
            isIgnore = track.isIgnore,
            isRadio = track.isRadio,
            duration = track.duration,
            favorite = track.favorite
        )

    fun toMediaItem(track: TrackModel): MediaItem  =
        MediaHandler.createMediaItem(
            id = track.id,
            fileName = track.fileName,
            duration = track.duration,
            favorite = track.favorite
        )

    fun toMediaItem(track: Track): MediaItem  =
        MediaHandler.createMediaItem(
            id = track.id,
            fileName = track.fileName,
            duration = track.duration,
            favorite = track.favorite
        )
}