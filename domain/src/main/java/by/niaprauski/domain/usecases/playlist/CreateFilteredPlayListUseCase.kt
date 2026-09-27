package by.niaprauski.domain.usecases.playlist

import by.niaprauski.domain.models.playlist.PlayListTrack
import by.niaprauski.domain.models.search.SearchTrackFilter
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.repository.PlayListRepository
import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.repository.TagRepository
import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateFilteredPlayListUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val trackRepository: TrackRepository,
    private val tagRepository: TagRepository,
    private val playListRepository: PlayListRepository,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend operator fun invoke(filter: SearchTrackFilter): Result<List<Track>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val settings = settingsRepository.get()
                val limit = settings.playListLimitSize

                val trackIds = if (filter.isTag) tagRepository.getTrackIdsByTagId(filter.tagId)
                else trackRepository.getTracksIdsByFilter(filter)

                if (trackIds.isEmpty()) return@runCatching emptyList()

                val tracksIdsShuffled = trackIds.shuffled().take(limit)
                val playList = trackRepository.getByIds(tracksIdsShuffled)
                playListRepository.overrideAll(tracksIdsShuffled.map { PlayListTrack(it) })

                playList
            }
        }
}