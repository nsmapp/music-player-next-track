package by.niaprauski.domain.usecases.track

import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetUnanalyzedTrackCountUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(): Flow<Int> =
        trackRepository.getUnanalyzedTrackCount()
            .flowOn(dispatcherProvider.io)


}