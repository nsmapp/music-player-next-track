package by.niaprauski.player.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import by.niaprauski.domain.usecases.tag.AnalyzeTrackMetadataUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class AnalyzeMetadataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val analyzeTrackMetadataUseCase: AnalyzeTrackMetadataUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return runCatching {
            var processedCount: Int
            do {
                val result = analyzeTrackMetadataUseCase(limit = TAG_LIMIT_ON_RUN)
                processedCount = result.getOrDefault(0)
                if (processedCount > 0) delay(DELAY_BETWEEN_RUN)

            } while (processedCount > 0)
            Result.success()
        }.getOrElse {
            Result.failure()
        }
    }

    companion object {
        private const val NAME = "analyze_track_metadata_work"
        private const val TAG_LIMIT_ON_RUN = 63
        private const val DELAY_BETWEEN_RUN = 1500L

        fun enqueue(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<AnalyzeMetadataWorker>()
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                NAME,
                ExistingWorkPolicy.KEEP,
                workRequest,
            )
        }
    }
}
