package by.niaprauski.domain.usecases.tag

import by.niaprauski.domain.models.tag.Tag
import by.niaprauski.domain.repository.TagRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchTagUseCase @Inject constructor(
    private val tagRepository: TagRepository,
    private val dispatcherProvider: DispatcherProvider,
) {
    suspend operator fun invoke(tag: String): Result<List<Tag>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                tagRepository.search(tag)
            }
        }
}