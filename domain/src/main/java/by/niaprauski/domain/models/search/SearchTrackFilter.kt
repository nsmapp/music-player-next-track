package by.niaprauski.domain.models.search

data class SearchTrackFilter(
    val text: String,
    val tagId: Long,
    val isTag: Boolean,
){
    companion object{
        val DEFAULT = SearchTrackFilter(
            text = "",
            tagId = -1L,
            isTag = false,
        )
    }
}