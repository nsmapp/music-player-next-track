package by.niaprauski.data.datastore.mapper

import by.niaprauski.data.datastore.AppSettingsEntity
import by.niaprauski.domain.models.AppSettings
import javax.inject.Inject

class SettingsMapper @Inject constructor() {

    fun toDomainModel(entity: AppSettingsEntity): AppSettings {
        return with(entity) {
            AppSettings(
                isShowWelcomeMessage = isWelcomeMessage,
                isVisuallyEnabled = isVisuallyEnabled,
                minDuration = minDuration,
                maxDuration = maxDuration,
                accentColorHex = accentColorHex,
                backgroundColorHex = backgroundColorHex,
                accentPosition = accentPosition,
                backgroundPosition = backgroundPosition,
                playListLimitSize = playlistLimitSize,
                isLikeTrackPriority = isLikeTrackPriority,
                likedTrackPercent = likedTrackPercent,
                isAutoPlayOnLaunch = isAutoPlayOnStart,
            )
        }
    }
}
