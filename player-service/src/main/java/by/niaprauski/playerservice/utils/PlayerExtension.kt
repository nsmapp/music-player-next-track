package by.niaprauski.playerservice.utils

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import by.niaprauski.utils.constants.TEXT_EMPTY
import by.niaprauski.utils.extension.fixOldEncoding
import by.niaprauski.utils.extension.ifNullOrEmpty
import by.niaprauski.utils.models.TRACK_KEY_FILE_NAME


fun ExoPlayer?.getMediaItemIndex(item: MediaItem): Int {
    val player = this ?: return -1
    for (i in 0 until player.mediaItemCount) {
        if (player.getMediaItemAt(i).mediaId == item.mediaId) {
            return i
        }
    }
    return -1
}

fun MediaMetadata.fix(fileName: String = TEXT_EMPTY): MediaMetadata {
    return buildUpon()
        .setTitle(title.fixOldEncoding().ifNullOrEmpty { fileName })
        .setArtist(artist.fixOldEncoding().ifNullOrEmpty { fileName })
        .build()
}

fun MediaItem.fixMetadata(): MediaItem {
    val fileName = mediaMetadata.extras?.getString(TRACK_KEY_FILE_NAME) ?: TEXT_EMPTY
    return buildUpon().setMediaMetadata(mediaMetadata.fix(fileName)).build()
}