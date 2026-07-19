package by.niaprauski.data.repoimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import by.niaprauski.data.database.dao.TrackDao
import by.niaprauski.data.mappers.TrackMapper
import by.niaprauski.domain.models.search.SearchTrackFilter
import by.niaprauski.domain.models.settings.PlayListConfig
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.models.track.TrackIds
import by.niaprauski.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackRepoImpl @Inject constructor(
    private val trackDao: TrackDao,
    private val trackMapper: TrackMapper
): TrackRepository {


    override fun saveTrackInfo(tracks: List<Track>) {
        val validPaths = tracks.map { it.id }
        val brokenTracksIds = trackDao.getBrokenTracksIds(validPaths)
        trackDao.deleteByIds(brokenTracksIds)

        val list = tracks.map { track -> trackMapper.toEntity(track) }
        trackDao.insertAll(list)
    }

    override fun getTrackIds(config: PlayListConfig): TrackIds {
        val unlikeIds = trackDao.getUnlikeIdsWithoutIgnored(
            minDuration = config.minDuration,
            maxDuration = config.maxDuration,
        )
        val likedIds = trackDao.getLikeIdsWithoutIgnored(
            minDuration = config.minDuration,
            maxDuration = config.maxDuration,
        )

        return TrackIds(unliked = unlikeIds, liked = likedIds)

    }

    override fun getByIds(playListIds: List<String>): List<Track> =
        trackDao.getTracksByIds(playListIds)
            .map { trackMapper.toModel(it) }

    override fun getPagedFlow(filter: SearchTrackFilter): Flow<PagingData<Track>> =
        Pager(
            config = PagingConfig(pageSize = 40, prefetchDistance = 10, initialLoadSize = 80, enablePlaceholders = false),
            pagingSourceFactory = { trackDao.getPagedFlow(filter.text) }
        ).flow.map { pagingData ->
            pagingData.map { trackMapper.toModel(it) }
        }

    override fun getTracksIdsByFilter(filter: SearchTrackFilter): List<String> =
        trackDao.getTracksIdsByFilter(filter.text)

    override fun markTrackAsIgnored(trackId: String) {
        trackDao.markTrackAsIgnore(trackId)
    }

    override fun unmarkTrackAsIgnored(trackId: String) {
        trackDao.unmarkTrackAsIgnore(trackId)
    }

    override fun upTrackFavorite(trackId: String, value: Int){
        trackDao.upTrackFavorite(trackId, value)
    }
    override fun getTrackById(trackId: String): Track? {
        val track = trackDao.getById(trackId) ?: return null
        return trackMapper.toModel(track)
    }

    override fun getUnanalyzedTrackCount(): Flow<Int> = trackDao.getUnanalyzedTrackCount()
}