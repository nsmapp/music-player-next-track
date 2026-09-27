package by.niaprauski.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import by.niaprauski.data.database.entity.PlayListTrackEntity

@Dao
interface PlayListDao{


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playList: PlayListTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playList: List<PlayListTrackEntity>)

    @Query("SELECT * FROM playlist")
    suspend fun getAll(): List<PlayListTrackEntity>

    @Query("DELETE FROM playlist WHERE track_id = :trackId")
    suspend fun deleteById(trackId: String)

    @Query("DELETE FROM playlist")
    suspend fun deleteAll()

    @Transaction
    suspend fun overrideAll(playList: List<PlayListTrackEntity>){
        deleteAll()
        insertAll(playList)
    }


}