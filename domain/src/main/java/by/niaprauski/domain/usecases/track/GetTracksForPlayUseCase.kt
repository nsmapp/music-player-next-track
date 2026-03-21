package by.niaprauski.domain.usecases.track

import by.niaprauski.domain.models.PlayListConfig
import by.niaprauski.domain.models.Track
import by.niaprauski.domain.repository.SettingsRepository
import by.niaprauski.domain.repository.TrackRepository
import by.niaprauski.domain.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTracksForPlayUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val trackRepository: TrackRepository,
    private val dispatcherProvider: DispatcherProvider,
) {

    suspend fun invoke(): Result<List<Track>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val settings = settingsRepository.get()

                val limit = settings.playListLimitSize
                val playlistConfig = PlayListConfig(
                    minDuration = settings.minDuration,
                    maxDuration = settings.maxDuration
                )
                val isLikeTrackPriority = settings.isLikeTrackPriority

                val trackIds = trackRepository.getTrackIds(playlistConfig)

                val playListIds = when {
                    limit >= trackIds.all.size -> trackIds.all
                    isLikeTrackPriority -> trackIds.all.take(limit)
                    else -> trackIds.all.shuffled().take(limit)
                }

                trackRepository.getByIds(playListIds).shuffled()
            }
        }
}