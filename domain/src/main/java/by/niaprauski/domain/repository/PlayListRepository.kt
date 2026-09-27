package by.niaprauski.domain.repository

import by.niaprauski.domain.models.playlist.PlayListTrack

interface PlayListRepository {

    suspend fun add(track: PlayListTrack)

    suspend fun addAll(tracks: List<PlayListTrack>)

    suspend fun getAll(): List<PlayListTrack>

    suspend fun remove(track: PlayListTrack)

    suspend fun removeAll()

    suspend fun overrideAll(tracks: List<PlayListTrack>)

}