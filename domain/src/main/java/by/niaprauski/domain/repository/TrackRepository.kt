package by.niaprauski.domain.repository

import androidx.paging.PagingData
import by.niaprauski.domain.models.search.SearchTrackFilter
import by.niaprauski.domain.models.settings.PlayListConfig
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.models.track.TrackIds
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun saveTrackInfo(tracks: List<Track>)

    fun getTrackIds(config: PlayListConfig): TrackIds

    fun getByIds(playListIds: List<String>): List<Track>

    fun getPagedFlow(filter: SearchTrackFilter): Flow<PagingData<Track>>

    fun getTracksIdsByFilter(filter: SearchTrackFilter): List<String>

    fun markTrackAsIgnored(trackId: String)

    fun unmarkTrackAsIgnored(trackId: String)

    fun upTrackFavorite(trackId: String, value: Int)

    fun getTrackById(trackId: String): Track?

    fun getUnanalyzedTrackCount(): Flow<Int>
}