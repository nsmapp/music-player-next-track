package by.niaprauski.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import by.niaprauski.data.database.entity.TagEntity
import by.niaprauski.data.database.entity.TrackEntity
import by.niaprauski.data.database.entity.TrackTagLinkEntity

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity): Long

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTags(tags: List<TagEntity>): List<Long>

    @Query("SELECT * FROM tags WHERE name IN (:names)")
    suspend fun getByNames(names: List<String>): List<TagEntity>

    @Query("SELECT * FROM tags WHERE name LIKE '%' || :query || '%' ORDER BY name ASC LIMIT 10")
    fun search(query: String): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLinks(links: List<TrackTagLinkEntity>)

    @Query("""
        SELECT tracks.* FROM tracks 
        INNER JOIN track_tag_link ON tracks.id = track_tag_link.track_id 
        WHERE track_tag_link.tag_id = :tagId AND track_tag_link.is_removed = 0
    """)
    fun getTracksPaged(tagId: Long): PagingSource<Int, TrackEntity>

    @Transaction
    suspend fun runInTransaction(block: suspend () -> Unit) = block()

    @Query("""
        SELECT tracks.id FROM tracks 
        INNER JOIN track_tag_link ON tracks.id = track_tag_link.track_id 
        WHERE track_tag_link.tag_id = :tagId AND track_tag_link.is_removed = 0 AND tracks.is_ignore = 0
        """)
    fun getTracksIdsByTagId(tagId: Long): List<String>
}