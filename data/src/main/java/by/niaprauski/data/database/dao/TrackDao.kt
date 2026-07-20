package by.niaprauski.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import by.niaprauski.data.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query(
        "SELECT id FROM tracks " +
                "WHERE is_ignore = 0 AND favorite = 0 " +
                "AND (duration >= :minDuration AND duration <= :maxDuration OR is_radio = 1) "
    )
    fun getUnlikeIdsWithoutIgnored(
        minDuration: Int, maxDuration: Int,
    ): List<String>

    @Query(
        "SELECT id FROM tracks " +
                "WHERE is_ignore = 0 AND favorite > 0 " +
                "AND (duration >= :minDuration AND duration <= :maxDuration OR is_radio = 1) " +
                "ORDER BY favorite ASC"
    )
    fun getLikeIdsWithoutIgnored(
        minDuration: Int, maxDuration: Int,
    ): List<String>

    @Query("SELECT * FROM tracks WHERE id IN (:ids) ORDER BY favorite DESC")
    fun getTracksByIds(ids: List<String>): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE name LIKE ('%' || :text || '%')")
    fun getPagedFlow(text: String): PagingSource<Int, TrackEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(tracks: List<TrackEntity>)

    @Query("SELECT id FROM tracks WHERE id NOT IN (:paths)")
    fun getBrokenTracksIds(paths: List<String>): List<String>

    @Query("DELETE FROM tracks WHERE id IN(:ids)")
    fun deleteByIds(ids: List<String>)

    @Query("UPDATE tracks SET is_ignore = 1 WHERE id = :trackId")
    fun markTrackAsIgnore(trackId: String)

    @Query("UPDATE tracks SET is_ignore = 0 WHERE id = :trackId")
    fun unmarkTrackAsIgnore(trackId: String)

    @Query("SELECT * FROM tracks WHERE id = :trackId")
    fun getById(trackId: String): TrackEntity?

    @Query("UPDATE tracks SET favorite = :value WHERE id = :trackId")
    fun upTrackFavorite(trackId: String, value: Int)

    @Query("SELECT * FROM tracks WHERE is_analyzed = 0 LIMIT :limit")
    fun getTracksForAnalysis(limit: Int): List<TrackEntity>

    @Query("UPDATE tracks SET is_analyzed = 1 WHERE id IN (:trackIds)")
    fun markAsAnalyzed(trackIds: List<String>)

    @Query("SELECT COUNT(*) FROM tracks WHERE is_analyzed = 0")
    fun getUnanalyzedTrackCount(): Flow<Int>

    @Query("SELECT id FROM tracks WHERE name LIKE ('%' || :text || '%') AND is_ignore = 0 ")
    fun getTracksIdsByFilter(text: String): List<String>

    @Query("DELETE FROM track_tag_link WHERE track_id IN (:brokenTracksIds)")
    fun deleteOldTags(brokenTracksIds: List<String>)

    @Transaction
    fun updateTracksLibrary(brokenIds: List<String>, newTracks: List<TrackEntity>) {
        deleteByIds(brokenIds)
        deleteOldTags(brokenIds)
        insertAll(newTracks)
    }
}