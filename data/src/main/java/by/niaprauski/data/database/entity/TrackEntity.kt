package by.niaprauski.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tracks", indices = [Index(value = ["id"], unique = true)])
data class TrackEntity(
    @PrimaryKey()
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo("is_ignore")
    val isIgnore: Boolean,
    @ColumnInfo("is_radio")
    val isRadio: Boolean,
    @ColumnInfo("duration")
    val duration: Long,
    @ColumnInfo("favorite")
    val favorite: Int,
    @ColumnInfo("is_analyzed")
    val isAnalyzed: Boolean = false,
)
