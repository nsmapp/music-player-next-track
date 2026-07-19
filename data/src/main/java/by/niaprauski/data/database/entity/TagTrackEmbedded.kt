package by.niaprauski.data.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TrackWithTags(
    @Embedded val track: TrackEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tag_id",
        associateBy = Junction(TrackTagLinkEntity::class, parentColumn = "track_id", entityColumn = "tag_id")
    )
    val tags: List<TagEntity>
)

data class TagWithTracks(
    @Embedded val tag: TagEntity,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "id",
        associateBy = Junction(TrackTagLinkEntity::class, parentColumn = "tag_id", entityColumn = "track_id")
    )
    val tracks: List<TrackEntity>
)