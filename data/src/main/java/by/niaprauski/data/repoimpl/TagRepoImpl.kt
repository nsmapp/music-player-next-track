package by.niaprauski.data.repoimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import by.niaprauski.data.database.dao.TagDao
import by.niaprauski.data.database.dao.TrackDao
import by.niaprauski.data.database.entity.TagEntity
import by.niaprauski.data.database.entity.TrackTagLinkEntity
import by.niaprauski.data.mappers.TagMapper
import by.niaprauski.data.mappers.TrackMapper
import by.niaprauski.domain.models.tag.Tag
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class TagRepoImpl @Inject constructor(
    private val tagDao: TagDao,
    private val trackDao: TrackDao,
    private val tagMapper: TagMapper,
    private val trackMapper: TrackMapper,
) : TagRepository {

    private val tagCache = ConcurrentHashMap<String, Long>()


    //TODO track repo?
    override fun getTracksByTagPaged(tagId: Long): Flow<PagingData<Track>> =
        Pager(
            config = PagingConfig(
                pageSize = 40,
                prefetchDistance = 10,
                initialLoadSize = 80,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { tagDao.getTracksPaged(tagId) }
        ).flow.map { pagingData ->
            pagingData.map { trackMapper.toModel(it) }
        }

    override fun getTrackIdsByTagId(tagId: Long): List<String> =
       tagDao.getTracksIdsByTagId(tagId)


    override fun search(tag: String): List<Tag> =
        tagDao.search(tag).map { tagMapper.toModel(it) }


    override suspend fun getTracksForAnalysis(limit: Int): List<Track> =
        trackDao.getTracksForAnalysis(limit).map { trackMapper.toModel(it) }

    override suspend fun saveAnalysisResults(results: Map<String, List<String>>) {
        val allTagNames = results.values.flatten().distinct()

        processTagsInCache(allTagNames)

        tagDao.runInTransaction {
            val allLinks: List<TrackTagLinkEntity> = results.flatMap { (trackId, tagNames) ->
                tagNames.map { name ->
                    TrackTagLinkEntity(trackId = trackId, tagId = tagCache[name] ?: 0L)
                }
            }
            tagDao.insertLinks(allLinks)
            trackDao.markAsAnalyzed(results.keys.toList())
        }
    }

    private suspend fun processTagsInCache(names: List<String>) {
        val missingNames = names.filter { !tagCache.containsKey(it) }
        if (missingNames.isEmpty()) return

        val existingTags = tagDao.getByNames(missingNames)
        existingTags.forEach { tag -> tagCache[tag.name] = tag.tagId }

        val missingTags = missingNames.filter { name ->
            existingTags.none { it.name == name }
        }

        if (missingTags.isNotEmpty()) {
            val newTags = missingTags.map { TagEntity(name = it) }
            tagDao.insertTags(newTags)

            tagDao.getByNames(missingTags).forEach { tag ->
                tagCache[tag.name] = tag.tagId
            }
        }
    }
}
