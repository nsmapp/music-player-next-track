package by.niaprauski.domain.usecases.settings

import by.niaprauski.domain.models.settings.AppSettings
import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetSettingsFlowUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend operator fun invoke(): Flow<AppSettings> =
        withContext(dispatcherProvider.io) {
            settingsRepository.getFlow()
        }
}