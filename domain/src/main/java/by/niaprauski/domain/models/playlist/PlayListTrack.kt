package by.niaprauski.domain.models.playlist

import by.niaprauski.domain.models.track.Track

data class PlayListTrack(
    val trackId: String
)

fun Track.toPlayListTrack() = PlayListTrack(this.id)