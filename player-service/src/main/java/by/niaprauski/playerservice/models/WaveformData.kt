package by.niaprauski.playerservice.models

data class WaveformData(
    val values: FloatArray
) {
    companion object {
        val DEFAULT = WaveformData(FloatArray(0))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WaveformData) return false
        return values.contentEquals(other.values)
    }

    override fun hashCode(): Int = values.contentHashCode()
}