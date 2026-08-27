package br.com.listennow.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class IncreaseSongTimesPlayedWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val songRepository: SongRepository,
    private val userRepository: UserRepository
): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val user = userRepository.findUser()

        songRepository.syncPendingTimesPlayedSongs(user!!.id)

        return Result.success()
    }

    companion object {
        const val WORK_NAME: String = "IncreaseSongTimesPlayedWorker"
    }
}