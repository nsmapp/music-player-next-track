package by.niaprauski.data.mappers

import by.niaprauski.data.database.entity.TagEntity
import by.niaprauski.domain.models.tag.Tag
import javax.inject.Inject

class TagMapper @Inject constructor() {

    fun toEntity(tag: Tag): TagEntity =
        TagEntity(
            name = tag.name
        )

    fun toModel(entity: TagEntity): Tag =
        Tag(
            tagId = entity.tagId,
            name = entity.name
        )
}
