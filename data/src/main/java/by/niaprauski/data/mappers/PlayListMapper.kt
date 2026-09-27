package by.niaprauski.data.mappers

import by.niaprauski.data.database.entity.PlayListTrackEntity
import by.niaprauski.domain.models.playlist.PlayListTrack
import javax.inject.Inject

class PlayListMapper @Inject constructor() {

    fun toEntity(model: PlayListTrack): PlayListTrackEntity =
        PlayListTrackEntity(
            trackId = model.trackId,
        )


    fun toModel(entity: PlayListTrackEntity): PlayListTrack =
        PlayListTrack(
            trackId = entity.trackId,
        )
}
