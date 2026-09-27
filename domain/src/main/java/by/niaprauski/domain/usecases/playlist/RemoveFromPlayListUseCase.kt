package by.niaprauski.domain.usecases.playlist

import by.niaprauski.domain.models.playlist.PlayListTrack
import by.niaprauski.domain.repository.PlayListRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveFromPlayListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend operator fun invoke(track: PlayListTrack): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {
                playListRepository.remove(track)
            }
        }
}
