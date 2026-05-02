package by.niaprauski.domain.usecases.track

import by.niaprauski.domain.models.PlayListConfig
import by.niaprauski.domain.models.Track
import by.niaprauski.domain.models.TrackIds
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

                val likeTrackPercent = settings.likedTrackPercent
                val limit = settings.playListLimitSize
                val playlistConfig = PlayListConfig(
                    minDuration = settings.minDuration,
                    maxDuration = settings.maxDuration
                )
                val isLikeTrackPriority = settings.isLikeTrackPriority

                val trackIds = trackRepository.getTrackIds(playlistConfig)

                val playListIds = when {
                    limit >= trackIds.all.size -> trackIds.all
                    isLikeTrackPriority -> prepareLikedPlayListIds(
                        limit = limit,
                        likeTrackPercent = likeTrackPercent,
                        trackIds = trackIds
                    )
                    else -> trackIds.all.shuffled().take(limit)
                }

                trackRepository.getByIds(playListIds).shuffled()
            }
        }

    private fun prepareLikedPlayListIds(
        limit: Int,
        likeTrackPercent: Int,
        trackIds: TrackIds
    ): List<String> {
        val likedTrackCount = limit * likeTrackPercent / 100
        val likedIds = trackIds.liked.shuffled().take(likedTrackCount)
        val unlikedIds = trackIds.unliked.shuffled().take(limit - likedIds.size)
        return likedIds + unlikedIds
    }
}