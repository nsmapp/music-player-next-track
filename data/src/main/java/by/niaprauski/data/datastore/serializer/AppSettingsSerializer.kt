package by.niaprauski.data.datastore.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import by.niaprauski.data.datastore.AppSettingsEntity
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer : Serializer<AppSettingsEntity> {

    override val defaultValue: AppSettingsEntity = AppSettingsEntity.getDefaultInstance().toBuilder()
        .setIsWelcomeMessage(true)
        .setIsVisuallyEnabled(false)
        .setMinDuration(20000)
        .setMaxDuration(900000)
        .setAccentColorHex("#E5E5E1")
        .setBackgroundColorHex("#FF65A591")
        .setAccentPosition(0f)
        .setBackgroundPosition(0.7f)
        .setPlaylistLimitSize(100)
        .setIsLikeTrackPriority(true)
        .build()

    override suspend fun readFrom(input: InputStream): AppSettingsEntity {
        try {
            return AppSettingsEntity.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: AppSettingsEntity, output: OutputStream) {
        t.writeTo(output)
    }
}