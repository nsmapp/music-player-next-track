package by.niaprauski.data.repoimpl

import by.niaprauski.data.database.dao.PlayListDao
import by.niaprauski.data.mappers.PlayListMapper
import by.niaprauski.domain.models.playlist.PlayListTrack
import by.niaprauski.domain.repository.PlayListRepository
import javax.inject.Inject

class PlayListRepoImpl @Inject constructor(
    private val playListDao: PlayListDao,
    private val playListMapper: PlayListMapper,
) : PlayListRepository {

    override suspend fun add(track: PlayListTrack) {
        playListDao.insert(playListMapper.toEntity(track))
    }

    override suspend fun addAll(tracks: List<PlayListTrack>) {
        playListDao.insertAll(tracks.map { playListMapper.toEntity(it) })
    }

    override suspend fun getAll(): List<PlayListTrack> {
        return playListDao.getAll().map { playListMapper.toModel(it) }
    }

    override suspend fun remove(track: PlayListTrack) {
        playListDao.deleteById(track.trackId)
    }

    override suspend fun removeAll() {
        playListDao.deleteAll()
    }

    override suspend fun overrideAll(tracks: List<PlayListTrack>) {
        playListDao.overrideAll(tracks.map { playListMapper.toEntity(it) })
    }
}
