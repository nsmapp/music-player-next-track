package by.niaprauski.domain.usecases.settings

import by.niaprauski.domain.models.settings.ColorPosition
import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SetBackgroundColorUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) {

    suspend operator fun invoke(colorHex: String, position: Float): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {

                settingsRepository.setBackgroundColorSettings(ColorPosition(colorHex, position))
            }
        }
}