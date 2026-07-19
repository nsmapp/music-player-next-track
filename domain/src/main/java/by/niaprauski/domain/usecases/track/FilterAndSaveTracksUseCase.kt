package by.niaprauski.domain.usecases.track

import by.niaprauski.domain.models.settings.AppSettings
import by.niaprauski.domain.models.track.Track
import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.usecases.settings.GetSettingsUseCase
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FilterAndSaveTracksUseCase @Inject constructor(
    private val trackRepository: TrackRepository,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend operator fun invoke(
        tracks: List<Track>,
    ): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val settingsResult = getSettingsUseCase()

                if (settingsResult.isSuccess) filterAndSave(settingsResult, tracks)
                else throw IllegalStateException("Settings not found")
            }
        }

    private fun filterAndSave(
        settingsResult: Result<AppSettings>,
        tracks: List<Track>
    ) {
        val settings = settingsResult.getOrThrow()
        val minDuration = settings.minDuration
        val maxDuration = settings.maxDuration
        val filteredTracks = tracks.filter { track ->
            track.duration in minDuration..maxDuration || track.isRadio
        }
        trackRepository.saveTrackInfo(filteredTracks)
    }
}