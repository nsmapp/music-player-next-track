package by.niaprauski.domain.usecases.settings

import by.niaprauski.domain.models.settings.AppSettings
import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend operator fun invoke(): Result<AppSettings> =
        withContext(dispatcherProvider.io) {
            runCatching { settingsRepository.get()}
        }
}