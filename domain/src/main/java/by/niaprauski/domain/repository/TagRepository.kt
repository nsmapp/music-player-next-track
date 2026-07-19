package by.niaprauski.domain.repository

import androidx.paging.PagingData
import by.niaprauski.domain.models.tag.Tag
import by.niaprauski.domain.models.track.Track
import kotlinx.coroutines.flow.Flow

interface TagRepository {

    fun getTracksByTagPaged(tagId: Long): Flow<PagingData<Track>>

    fun getTrackIdsByTagId(tagId: Long): List<String>

    fun search(tag: String): List<Tag>

    suspend fun saveAnalysisResults(results: Map<String, List<String>>)

    suspend fun getTracksForAnalysis(limit: Int): List<Track>
}