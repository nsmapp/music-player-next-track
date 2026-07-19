package by.niaprauski.data.mappers

import by.niaprauski.data.database.entity.TrackEntity
import by.niaprauski.domain.models.track.Track
import javax.inject.Inject

class TrackMapper @Inject constructor(){

    fun toEntity(track :Track): TrackEntity =
        with(track) {
            TrackEntity(
                id = id,
                name = fileName,
                isIgnore = isIgnore,
                isRadio = isRadio,
                duration = duration,
                favorite = favorite,
            )
        }

    fun toModel(track : TrackEntity): Track =
        with(track) {
            Track(
                id = id,
                fileName = name,
                isIgnore = isIgnore,
                isRadio = isRadio,
                duration = duration,
                favorite = favorite,
            )
        }


}