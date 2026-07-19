package by.niaprauski.domain.models.track

data class Track(
    val id: String,
    val fileName: String,
    val isIgnore: Boolean,
    val isRadio: Boolean,
    val duration: Long,
    val favorite: Int,
)