package by.niaprauski.utils.media

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import by.niaprauski.domain.models.track.TrackMetadata
import by.niaprauski.domain.utils.DispatcherProvider
import by.niaprauski.domain.utils.MetadataProvider
import by.niaprauski.utils.constants.TEXT_EMPTY
import by.niaprauski.utils.extension.fixOldEncoding
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class MetadataProviderImpl(
    private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
) : MetadataProvider {

    private val semaphore = Semaphore(8)

    override suspend fun getMetadata(uris: List<String>): Map<String, TrackMetadata> =
        coroutineScope {
            uris.map { uri ->
                async(dispatcherProvider.io) {
                    semaphore.withPermit {
                        val retriever = MediaMetadataRetriever()
                        try {
                            retriever.setDataSource(context, uri.toUri())
                            uri to TrackMetadata(
                                artist = retriever
                                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                                    .fixOldEncoding()?.toString() ?: TEXT_EMPTY,
                                album = retriever
                                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                                    .fixOldEncoding()?.toString() ?: TEXT_EMPTY,
                                style = retriever
                                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                                    .fixOldEncoding()?.toString() ?: TEXT_EMPTY,
                                year = retriever
                                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                                    .fixOldEncoding()?.toString() ?: TEXT_EMPTY,
                            )
                        } catch (e: Exception) {
                            uri to TrackMetadata(TEXT_EMPTY, TEXT_EMPTY, TEXT_EMPTY, TEXT_EMPTY)
                        } finally {
                            retriever.release()
                        }
                    }
                }
            }.awaitAll().toMap()
        }
}
