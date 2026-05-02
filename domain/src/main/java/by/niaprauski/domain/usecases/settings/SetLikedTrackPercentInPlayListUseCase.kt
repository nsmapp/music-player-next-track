package by.niaprauski.domain.usecases.settings

import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetLikedTrackPercentInPlayListUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend operator fun invoke(percent: Int): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {
                settingsRepository.setLikedTrackPercent(percent)
            }
        }
}