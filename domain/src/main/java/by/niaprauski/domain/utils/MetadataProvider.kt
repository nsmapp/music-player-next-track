package by.niaprauski.domain.utils

import by.niaprauski.domain.models.track.TrackMetadata

interface MetadataProvider {
    suspend fun getMetadata(uris: List<String>): Map<String, TrackMetadata>
}
