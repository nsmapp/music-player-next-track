package by.niaprauski.domain.usecases.track

import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeTrackFavoriteUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
    private val dispatcherProvider: DispatcherProvider
)  {

    suspend fun invoke(trackId: String): Result<Unit> = withContext(dispatcherProvider.io){
        runCatching {
            val track = trackRepository.getTrackById(trackId) ?: return@runCatching
            val newFavoriteValue = if (track.favorite > 0) 0 else 1
            trackRepository.upTrackFavorite(trackId, newFavoriteValue)
        }
    }
}