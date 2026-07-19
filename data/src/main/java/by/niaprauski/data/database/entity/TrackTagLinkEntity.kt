package by.niaprauski.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "track_tag_link",
    primaryKeys = ["track_id", "tag_id"],
    indices = [Index("tag_id")]
)
data class TrackTagLinkEntity(
    @ColumnInfo(name = "track_id")
    val trackId: String,
    @ColumnInfo(name = "tag_id")
    val tagId: Long,
    @ColumnInfo(name = "is_removed")
    val isRemoved: Boolean = false
)