package by.niaprauski.domain.usecases.tag

import androidx.paging.PagingData
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.repository.TagRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetTracksByTagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    operator fun invoke(tagId: Long): Flow<PagingData<Track>> =
        tagRepository.getTracksByTagPaged(tagId)
            .flowOn(dispatcherProvider.io)
}