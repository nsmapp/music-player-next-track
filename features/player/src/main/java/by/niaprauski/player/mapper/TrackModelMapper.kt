package by.niaprauski.player.mapper

import androidx.media3.common.MediaItem
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.utils.DispatcherProvider
import by.niaprauski.utils.media.MediaHandler
import by.niaprauski.utils.models.ITrack
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrackModelMapper @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) {

    suspend fun toDomainModels(tracks: List<ITrack>): List<Track> =
        withContext(dispatcherProvider.io) {
            tracks.map { track ->
                with(track) {
                    Track(
                        id = id,
                        fileName = fileName,
                        isIgnore = false,
                        isRadio = isRadio,
                        duration = duration,
                        favorite = 0, //TODO ?
                    )
                }
            }
        }


    suspend fun toMediaItems(tracks: List<Track>): List<MediaItem> =
        withContext(dispatcherProvider.io) {
            tracks.map { track ->

                MediaHandler.createMediaItem(
                    id = track.id,
                    fileName = track.fileName,
                    duration = track.duration,
                    favorite = track.favorite
                )
            }
        }
}