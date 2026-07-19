package by.niaprauski.domain.models.track

data class TrackIds(
    val unliked: List<String>,
    val liked: List<String>
){

    val all: List<String> by lazy {  liked + unliked }
}
