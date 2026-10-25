package by.niaprauski.domain.usecases.playlist

import by.niaprauski.domain.models.playlist.PlayListTrack
import by.niaprauski.domain.repository.PlayListRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OverridePlayListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend operator fun invoke(tracks: List<PlayListTrack>): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {
                if (tracks.isNotEmpty()) playListRepository.overrideAll(tracks)
            }
        }
}
