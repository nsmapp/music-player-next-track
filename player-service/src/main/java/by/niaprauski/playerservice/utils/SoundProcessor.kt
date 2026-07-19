package by.niaprauski.playerservice.utils

import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.AudioProcessor.EMPTY_BUFFER
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder

@UnstableApi
class SoundProcessor(
    private val scope: CoroutineScope,
    private val waveForm: MutableStateFlow<FloatArray>,
) : AudioProcessor {

    private val waveChannel = Channel<FloatArray>(Channel.CONFLATED)

    private var isVisuallyEnabled = false
    private var lastProcessTime = 0L
    private var chank: Int = 64

    private var inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    private var outputBuffer: ByteBuffer = EMPTY_BUFFER
    private var buffer: ByteBuffer = EMPTY_BUFFER
    private var inputEnded = false

    init {
        scope.launch {
            for (array in waveChannel) {
                waveForm.update { array }
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
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastProcessTime >= 30) {
                lastProcessTime = currentTime
                waveChannel.trySend(extractWaveform(inputBuffer))
            }
        }

        if (buffer.capacity() < count) buffer = ByteBuffer
            .allocateDirect(count)
            .order(ByteOrder.nativeOrder())
        else buffer.clear()

        buffer.put(inputBuffer)
        buffer.flip()
        outputBuffer = buffer
    }

    private fun extractWaveform(buffer: ByteBuffer): FloatArray {
        val dup = buffer.duplicate().order(ByteOrder.nativeOrder())
        val limit = dup.limit()
        val totalSamples = dup.remaining() / 2
        val targetPoints = chank

        val actualPoints = if (totalSamples < targetPoints) totalSamples else targetPoints
        val waveArray = FloatArray(targetPoints)
        if (actualPoints <= 0) return waveArray

        val step = totalSamples / actualPoints

        for (i in 0 until actualPoints) {
            val position = (i * step) * 2
            if (position + 1 < limit) {
                val sample = dup.getShort(position).toFloat() / Short.MAX_VALUE
                waveArray[i] = sample
            }
        }
        return waveArray
    }

    override fun queueEndOfStream() {
        inputEnded = true
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
        inputEnded = false
    }

    override fun reset() {
        flush()
        buffer = EMPTY_BUFFER
        inputAudioFormat = AudioProcessor.AudioFormat.NOT_SET
    }

    fun setIsVisuallyEnabled(enabled: Boolean) {
        isVisuallyEnabled = enabled
    }

    fun setChank(chank: Int) {
        this.chank = chank
    }
}