package by.niaprauski.playerservice.utils

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.EMPTY_BUFFER
import androidx.media3.common.util.UnstableApi
import by.niaprauski.playerservice.models.WaveformData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder

@UnstableApi
class SoundProcessor(
    private val scope: CoroutineScope,
    private val waveForm: MutableStateFlow<WaveformData>,
) : AudioProcessor {

    companion object {
        const val CHUNK_COUNT: Int = 64
        private const val DEFAULT_RATE: Int = 44100
        private const val DEFAULT_CHANNEL_COUNT: Int = 2
        private const val BYTES_PER_16BIT_SAMPLE: Int = 2
        private const val FRAME_INTERVAL_SECONDS: Double = 0.060
        private const val FRAME_INTERVAL_MS: Long = 60L
        private const val MIN_CHUNK_BYTES: Int = 512
        private const val MIN_REMAINING_SLICE_BYTES: Int = 128
    }

    private val waveChannel = Channel<FloatArray>(capacity = 60)

    private var isVisuallyEnabled = false

    private var inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    private var outputBuffer: ByteBuffer = EMPTY_BUFFER
    private var buffer: ByteBuffer = EMPTY_BUFFER
    private var inputEnded = false

    init {
        scope.launch {
            for (array in waveChannel) {
                waveForm.value = WaveformData(array)
                delay(FRAME_INTERVAL_MS)
            }
        }
    }

    override fun configure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        this.inputAudioFormat = inputAudioFormat
        return inputAudioFormat
    }

    override fun isActive(): Boolean {
        return inputAudioFormat != AudioProcessor.AudioFormat.NOT_SET
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        if (!inputBuffer.hasRemaining()) return

        val count = inputBuffer.remaining()
        if (count == 0) return

        if (isVisuallyEnabled) {
            processAudioBuffer(inputBuffer)
        }

        if (buffer.capacity() < count) buffer = ByteBuffer
            .allocateDirect(count)
            .order(ByteOrder.nativeOrder())
        else buffer.clear()

        buffer.put(inputBuffer)
        buffer.flip()
        outputBuffer = buffer
    }

    private fun processAudioBuffer(buffer: ByteBuffer) {
        val dup = buffer.duplicate().order(ByteOrder.nativeOrder())
        val totalBytes = dup.remaining()
        if (totalBytes <= 0) return

        val sampleRate = if (inputAudioFormat.sampleRate > 0) inputAudioFormat.sampleRate else DEFAULT_RATE
        val channelCount = if (inputAudioFormat.channelCount > 0) inputAudioFormat.channelCount else DEFAULT_CHANNEL_COUNT
        val bytesPerFrame = (sampleRate * channelCount * BYTES_PER_16BIT_SAMPLE * FRAME_INTERVAL_SECONDS).toInt().coerceAtLeast(MIN_CHUNK_BYTES)

        var offset = 0
        while (offset < totalBytes) {
            val chunkSize = (totalBytes - offset).coerceAtMost(bytesPerFrame)
            if (chunkSize < MIN_REMAINING_SLICE_BYTES) break

            dup.position(offset)
            dup.limit(offset + chunkSize)

            val frame = extractWaveformFromSlice(dup)
            waveChannel.trySend(frame)

            offset += chunkSize
        }
    }

    private fun extractWaveformFromSlice(slice: ByteBuffer): FloatArray {
        val totalSamples = slice.remaining() / 2
        val targetPoints = CHUNK_COUNT

        val actualPoints = if (totalSamples < targetPoints) totalSamples else targetPoints
        val waveArray = FloatArray(targetPoints)
        if (actualPoints <= 0) return waveArray

        val step = totalSamples / actualPoints

        for (i in 0 until actualPoints) {
            val position = slice.position() + (i * step) * 2
            if (position + 1 < slice.limit()) {
                val sample = slice.getShort(position).toFloat() / Short.MAX_VALUE
                waveArray[i] = sample
            }
        }
        return waveArray
    }

    override fun queueEndOfStream() {
        inputEnded = true
        clearChannel()
    }

    override fun getOutput(): ByteBuffer {
        val output = outputBuffer
        outputBuffer = EMPTY_BUFFER
        return output
    }

    override fun isEnded(): Boolean {
        return inputEnded && outputBuffer === EMPTY_BUFFER
    }

    override fun flush() {
        outputBuffer = EMPTY_BUFFER
        clearChannel()
        inputEnded = false
    }

    override fun reset() {
        flush()
        buffer = EMPTY_BUFFER
        inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    }

    private fun clearChannel() {
        while (waveChannel.tryReceive().isSuccess) {

        }
        waveForm.value = WaveformData.DEFAULT
    }

    fun setIsVisuallyEnabled(enabled: Boolean) {
        isVisuallyEnabled = enabled
        if (!enabled) clearChannel()

    }
}