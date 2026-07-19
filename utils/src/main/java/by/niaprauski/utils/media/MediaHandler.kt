package by.niaprauski.utils.media

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns._ID
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.DISPLAY_NAME
import android.provider.MediaStore.Files.FileColumns.MIME_TYPE
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import by.niaprauski.utils.constants.TEXT_EMPTY
import by.niaprauski.utils.models.ITrack
import by.niaprauski.utils.models.MimeType
import by.niaprauski.utils.models.TRACK_KEY_FAVORITE
import by.niaprauski.utils.models.TRACK_KEY_FILE_NAME
import by.niaprauski.utils.models.TRACK_KEY_ID
import java.io.BufferedReader
import java.io.InputStreamReader

object MediaHandler {

    val radioMimeTypes = arrayOf(
        MimeType.PLS.type,
        MimeType.M3U.type,
        MimeType.M3U8.type
    )

    val audioMimeTypes = arrayOf(
        MimeType.OGG.type,
        MimeType.MPEG.type,
        MimeType.OPUS.type,
        MimeType.AAC.type,
        MimeType.FLAC.type,
        MimeType.WAV.type,
        MimeType.M4A.type
    )

    fun getTrackData(
        cr: ContentResolver,
    ): List<ITrack> {

        val iTracks = mutableListOf<ITrack>()

        val projection: Array<String> = arrayOf(
            _ID,
            DISPLAY_NAME,
            MIME_TYPE,
            MediaStore.Audio.Media.DURATION,
        )


        val playlistSelection = radioMimeTypes.joinToString(",") { "?" }
        val audioSelection = audioMimeTypes.joinToString(",") { "?" }

        val selection = "($MIME_TYPE IN ($playlistSelection)) OR ($MIME_TYPE IN ($audioSelection))"
        val selectionArgs = radioMimeTypes + audioMimeTypes

        val cursor =
            cr.query(/* uri = */ MediaStore.Files.getContentUri("external"),/* projection = */
                projection,/* selection = */
                selection,/* selectionArgs = */
                selectionArgs,/* sortOrder = */
                null
            )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = c.getLong(c.getColumnIndexOrThrow(_ID)) ?: continue
                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                val displayName =
                    c.getStringOrNull(c.getColumnIndexOrThrow(DISPLAY_NAME)) ?: TEXT_EMPTY
                val mimeType = c.getStringOrNull(c.getColumnIndexOrThrow(MIME_TYPE)) ?: TEXT_EMPTY
                val duration =
                    c.getLongOrNull(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))


                val isRadio = when (mimeType) {
                    in radioMimeTypes -> true
                    else -> false
                }

                val urlOrPath: String? = if (!isRadio) uri.toString()
                else {
                    val contentUri = getRadioStreamUrlByIdOrNull(id)
                    parsePlaylistForStreamUrl(contentUri, cr)
                }

                if (urlOrPath != null) {
                    val iTrack = object : ITrack {
                        override val fileName = displayName
                        override val artist = TEXT_EMPTY
                        override val id = urlOrPath ?: TEXT_EMPTY
                        override val isRadio = isRadio
                        override val duration = duration ?: 0L
                    }
                    iTracks.add(iTrack)
                }
            }
        }
        cursor?.close()

        return iTracks
    }

    private fun getRadioStreamUrlByIdOrNull(id: Long): Uri =
        ContentUris
            .withAppendedId(MediaStore.Files.getContentUri("external"), id)

    fun parsePlaylistForStreamUrl(playlistUri: Uri, cr: ContentResolver): String? {

        val mimeType = cr.getType(playlistUri)
        val inputStream = cr.openInputStream(playlistUri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line: String?

        try {
            while (reader.readLine().also { line = it } != null) {
                val trimmedLine = line?.trim() ?: continue
                val streamUrl = when (mimeType) {
                    MimeType.M3U.type, MimeType.M3U8.type -> parseM3uLine(trimmedLine)
                    MimeType.PLS.type -> parsePlsLine(trimmedLine)
                    else -> null
                }

                if (streamUrl != null) return streamUrl

            }
        } finally {
            inputStream?.close()
            reader.close()
        }

        return null
    }

    private fun parsePlsLine(line: String): String? {
        if (line.startsWith("File", ignoreCase = true)) {
            val parts = line.split('=', limit = 2)
            if (parts.size == 2) {
                val url = parts[1].trim()
                if (url.startsWith("http")) return url
            }
        }
        return null
    }

    private fun parseM3uLine(line: String): String? = if (line.startsWith("http")) line else null

    fun uriToMediaItemFromIntent(uri: Uri, cr: ContentResolver? = null): MediaItem {
        var fileName = uri.lastPathSegment ?: TEXT_EMPTY
        fileName = tryGetFileName(cr, uri, fileName)

        return createMediaItem(uri.toString(), fileName, 0L, 0)
    }

    fun radioUriToMediaItem(uri: Uri, cr: ContentResolver): MediaItem? {
        var fileName = uri.lastPathSegment ?: TEXT_EMPTY
        fileName = tryGetFileName(cr, uri, fileName)

        val streamUrl = parsePlaylistForStreamUrl(uri, cr) ?: return null

        return createMediaItem(streamUrl, fileName, 0L, 0)
    }

    private fun tryGetFileName(
        cr: ContentResolver?,
        uri: Uri,
        fileName: String
    ): String {
        var fileName1 = fileName
        cr?.query(uri, arrayOf(DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(DISPLAY_NAME)
                    if (index != -1) {
                        fileName1 = cursor.getString(index)
                    }
                }
            }
        return fileName1
    }

    fun createMediaItem(
        id: String,
        fileName: String,
        duration: Long,
        favorite: Int,
    ): MediaItem {

        val extras = Bundle().apply {
            putString(TRACK_KEY_ID, id)
            putInt(TRACK_KEY_FAVORITE, favorite)
            putString(TRACK_KEY_FILE_NAME, fileName)
        }

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setDurationMs(duration)
                    .setExtras(extras)
                    .build()
            )
            .build()
    }
}