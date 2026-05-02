package by.niaprauski.domain.repository

import by.niaprauski.domain.models.AppSettings
import by.niaprauski.domain.models.ColorPosition
import kotlinx.coroutines.flow.Flow


interface SettingsRepository {

    suspend fun getFlow(): Flow<AppSettings>

    suspend fun get(): AppSettings

    suspend fun setShowWelcomeMessage(isFirstLaunch: Boolean)

    suspend fun setVisuallyEnabled(enabled: Boolean)

    suspend fun setMinDuration(duration: Int)

    suspend fun setMaxDuration(duration: Int)

    suspend fun setAccentColorSettings(position: ColorPosition)

    suspend fun setBackgroundColorSettings(position: ColorPosition)

    suspend fun getTrackLimit(): Int

    suspend fun setTrackLimit(count: Int)

    suspend fun setLikedTrackPercent(percent: Int)

    suspend fun setLikeTrackPriority(isLikeTrackPriority: Boolean)

    suspend fun setAutoPlay(isAutoPlay: Boolean)


}