package by.niaprauski.playerservice.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaButtonReceiver
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import by.niaprauski.translations.R
import by.niaprauski.utils.extension.fixOldEncoding
import by.niaprauski.utils.extension.getFileName
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

        val mediaMetadata = player?.currentMediaItem?.mediaMetadata

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setShowWhen(false)
            .setContentIntent(createOpenActivityIntent(context))

        setTrackInformation(context, mediaMetadata, builder)
        setStyle(mediaSession, builder)
        setActions(context, player, builder)

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

    private fun setActions(
        context: Context,
        player: ExoPlayer?,
        builder: NotificationCompat.Builder
    ) {
        val playPausePendingIntent: PendingIntent = createPendingIntent(
            context = context,
            code = KeyEvent.KEYCODE_MEDIA_PLAY,
            requestCode = REQUEST_CODE_PLAY_PAUSE
        )

        val nextPendingIntent: PendingIntent = createPendingIntent(
            context = context,
            code = KeyEvent.KEYCODE_MEDIA_NEXT,
            requestCode = REQUEST_CODE_NEXT
        )

        val prevPendingIntent: PendingIntent = createPendingIntent(
            context = context,
            code = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            requestCode = REQUEST_CODE_PREV
        )

        val trackBackText =
            context.getString(R.string.feature_play_service_track_back)
        val pauseText =
            context.getString(R.string.feature_play_service_pause)
        val playText =
            context.getString(R.string.feature_play_service_play)
        val nextText =
            context.getString(R.string.feature_play_service_next)

        val playPauseIconId =
            if (player?.isPlaying == true) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseText = if (player?.isPlaying == true) pauseText else playText
        val playPauseIntent = playPausePendingIntent

        builder
            .addAction(android.R.drawable.ic_media_previous, trackBackText, prevPendingIntent)
            .addAction(playPauseIconId, playPauseText, playPauseIntent)
            .addAction(android.R.drawable.ic_media_next, nextText, nextPendingIntent)
    }

    private fun setTrackInformation(
        context: Context,
        mediaMetadata: MediaMetadata?,
        builder: NotificationCompat.Builder
    ) {
        val trackDefaultTitle =
            context.getString(R.string.feature_player_service_next_track)
        val trackDefaultArtist =
            context.getString(R.string.feature_player_service_next_artist)
        val trackTitle =
            mediaMetadata?.title.fixOldEncoding() ?: mediaMetadata?.getFileName(trackDefaultTitle)
        val trackArtist =
            mediaMetadata?.artist.fixOldEncoding() ?: mediaMetadata?.getFileName(trackDefaultArtist)
        builder.setContentTitle(trackTitle)
            .setContentText(trackArtist)
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

    @OptIn(UnstableApi::class)
    private fun createPendingIntent(
        context: Context,
        code: Int,
        requestCode: Int
    ): PendingIntent {
        val playIntent = Intent(context, MediaButtonReceiver::class.java)
            .apply {
                setAction(Intent.ACTION_MEDIA_BUTTON)
                putExtra(
                    Intent.EXTRA_KEY_EVENT,
                    KeyEvent(KeyEvent.ACTION_DOWN, code)
                )
            }
        val playPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                requestCode,
                playIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        return playPendingIntent
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