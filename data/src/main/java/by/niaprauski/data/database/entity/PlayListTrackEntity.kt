package by.niaprauski.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "playlist")
data class PlayListTrackEntity(
    @ColumnInfo("track_id")
    val trackId: String,
){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    var id: Long = 0
}
