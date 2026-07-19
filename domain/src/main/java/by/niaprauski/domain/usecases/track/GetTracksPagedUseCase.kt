package by.niaprauski.domain.usecases.track

import androidx.paging.PagingData
import by.niaprauski.domain.models.search.SearchTrackFilter
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTracksPagedUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend operator fun invoke(filter: SearchTrackFilter): Flow<PagingData<Track>> =
        withContext(dispatcherProvider.io) {
            trackRepository.getPagedFlow(filter)
        }

}