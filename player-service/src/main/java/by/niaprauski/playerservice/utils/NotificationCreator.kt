package by.niaprauski.playerservice.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import by.niaprauski.translations.R
import by.niaprauski.utils.intents.OpenAppIntent

//TODO? migrate to MediaNotification.Provider
class NotificationCreator {

    companion object {
        const val channelId = "by.niaprauski.nexttrack.chanel"
        private const val REQUEST_CODE_PLAY_PAUSE = 0
        private const val REQUEST_CODE_NEXT = 1
        private const val REQUEST_CODE_PREV = 2
    }

    @OptIn(UnstableApi::class)
    fun buildNotification(
        context: Context,
        player: ExoPlayer?,
        mediaSession: MediaSession?
    ): Notification {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setShowWhen(false)
            .setContentIntent(createOpenActivityIntent(context))

        setStyle(mediaSession, builder)

        return builder.build()
    }

    @OptIn(UnstableApi::class)
    private fun setStyle(
        mediaSession: MediaSession?,
        builder: NotificationCompat.Builder
    ) {
        mediaSession?.let { session ->
            val style = MediaStyleNotificationHelper.MediaStyle(session)
                .setShowActionsInCompactView(0, 1, 2)
            builder.setStyle(style)
        }
    }

    private fun createOpenActivityIntent(context: Context): PendingIntent {
        val openActivityIntent = Intent(
            OpenAppIntent.OPEN_APP_ACTION
        ).apply {
            `package` = context.packageName
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppIntent = PendingIntent.getActivity(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */ openActivityIntent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return openAppIntent
    }


    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(channelId) == null) {
            val name = context.getString(R.string.feature_play_service_next_track_channel)
            val descriptionText =
                context.getString(R.string.feature_play_service_next_track_music_chanel)
            val channel =
                NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_LOW)
                    .apply { description = descriptionText }

            notificationManager.createNotificationChannel(channel)
        }
    }

}