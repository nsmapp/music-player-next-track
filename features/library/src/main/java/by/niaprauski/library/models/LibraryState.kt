package by.niaprauski.library.models

import androidx.compose.runtime.Stable
import by.niaprauski.domain.models.tag.Tag
import by.niaprauski.utils.constants.TEXT_EMPTY

@Stable
data class LibraryState(
    val searchText: String,
    val tags: List<Tag>,
    val unanalyzedTrackCount: Int,
){
    companion object{
        val INITIAL = LibraryState(
            searchText = TEXT_EMPTY,
            tags = emptyList(),
            unanalyzedTrackCount = 0,
        )
    }
}
