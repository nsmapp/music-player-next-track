package by.niaprauski.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class TagEntity(
    @ColumnInfo(name = "name")
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tag_id")
    var tagId: Long = 0
}
