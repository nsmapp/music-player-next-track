package by.niaprauski.domain.usecases.track

import by.niaprauski.domain.models.search.SearchTrackFilter
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.repository.TagRepository
import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFilteredTracksForPlayUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val trackRepository: TrackRepository,
    private val tagRepository: TagRepository,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend fun invoke(filter: SearchTrackFilter): Result<List<Track>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val settings = settingsRepository.get()
                val limit = settings.playListLimitSize

                val tracksIds = if (filter.isTag) tagRepository.getTrackIdsByTagId(filter.tagId)
                else trackRepository.getTracksIdsByFilter(filter)

                trackRepository.getByIds(tracksIds.shuffled().take(limit))
            }
        }


}