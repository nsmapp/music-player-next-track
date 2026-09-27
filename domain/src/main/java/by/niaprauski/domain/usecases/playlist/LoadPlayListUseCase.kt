package by.niaprauski.domain.usecases.playlist

import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.repository.PlayListRepository
import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadPlayListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    private val trackRepository: TrackRepository,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend operator fun invoke(): Result<List<Track>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val trackIds = playListRepository.getAll().map { it.trackId }
                if (trackIds.isEmpty()) return@runCatching emptyList()

                val tracksMap = trackRepository.getByIds(trackIds).associateBy { it.id }
                trackIds.mapNotNull { tracksMap[it] }
            }
        }
}
