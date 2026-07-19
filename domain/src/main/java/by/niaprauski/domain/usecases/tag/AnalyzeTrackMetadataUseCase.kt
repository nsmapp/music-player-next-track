package by.niaprauski.domain.usecases.tag

import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.models.track.TrackMetadata
import by.niaprauski.domain.repository.TagRepository
import by.niaprauski.domain.utils.DispatcherProvider
import by.niaprauski.domain.utils.MetadataProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnalyzeTrackMetadataUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val metadataProvider: MetadataProvider,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend operator fun invoke(limit: Int): Result<Int> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val tracks = tagRepository.getTracksForAnalysis(limit)
                if (tracks.isEmpty()) return@runCatching 0

                val audioFiles = tracks.filter { it.isRadio.not() }

                val metadataMap = if (audioFiles.isNotEmpty())
                    metadataProvider.getMetadata(audioFiles.map { it.id })
                else emptyMap()


                val results = tracks.associate { track ->
                    val tags = if (track.isRadio) listOf(TAG_RADIO)
                    else extractTags(track, metadataMap[track.id])

                    track.id to tags
                }

                tagRepository.saveAnalysisResults(results)
                tracks.size
            }
        }

    private fun extractTags(track: Track, metadata: TrackMetadata?): List<String> {
        if (track.isRadio) return listOf(TAG_RADIO)
        if (metadata == null) return listOf(TAG_UNKNOWN)

        return listOfNotNull(
            metadata.artist.clean(),
            metadata.album.clean(),
            metadata.style.clean(),
            metadata.year.clean(),
        ).ifEmpty { listOf(TAG_UNKNOWN) }
    }

    private fun String?.clean(): String? = this
        ?.lowercase()
        ?.filter { it.isLetterOrDigit() }
        ?.takeIf { it.isNotBlank() }

    companion object {
        private const val TAG_RADIO = "radio"
        private const val TAG_UNKNOWN = "unknown"
    }
}
