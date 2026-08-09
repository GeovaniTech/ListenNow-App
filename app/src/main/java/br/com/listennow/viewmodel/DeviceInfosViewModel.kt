package br.com.listennow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.concurrent.AtomicBoolean
import br.com.listennow.R
import br.com.listennow.extensions.isValidUUID
import br.com.listennow.repository.PlaylistRepository
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import br.com.listennow.utils.NetworkUtil
import br.com.listennow.webclient.playlist.model.PlaylistCountRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceInfosViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
) : CommonViewModel(userRepository) {

    private var _userId: MutableLiveData<String> = MutableLiveData()
    val userId: LiveData<String> get() = _userId

    private var _showDialogConfirmImportData: MutableLiveData<AtomicBoolean> = MutableLiveData(
        AtomicBoolean(false))
    val showDialogConfirmImportData: LiveData<AtomicBoolean> get() = _showDialogConfirmImportData

    var idUserWithData: String? = null

    lateinit var songsIds: List<String>
    var countPlaylistToImport: Int = 0

    suspend fun loadUserId() {
        _userId.postValue(userRepository.findUser()!!.id)
    }

    suspend fun getSongIdsSongsByUser(userReceiver: String, userWithSongs: String): List<String>? {
        return songRepository.getIdsSongsFromAnotherUser(userReceiver, userWithSongs)
    }

    suspend fun getCountPlaylistsToImport(userWithData: String): Int {
        return playlistRepository.getCountPlaylistsToImport(
            PlaylistCountRequest(
                clientReceiverId = user!!.id,
                clientWithPlaylistsId = userWithData
            )
        )
    }

    suspend fun checkIsPossibleImportData() {
        try {
            if (idUserWithData.isNullOrEmpty()) {
                updateExceptionResMessage(R.string.fragment_device_infos_device_id_must_not_be_null)
                return
            }

            if (!idUserWithData!!.isValidUUID()) {
                updateExceptionResMessage(R.string.invalid_device_id)
                return
            }

            if (idUserWithData == user?.id!!) {
                updateExceptionResMessage(R.string.fragment_Device_infos_device_id_must_be_different_than_yours)
                return
            }

            val songsIds = songRepository.getIdsSongsFromAnotherUser(user?.id!!, idUserWithData!!)

            val countPlaylistToImport = playlistRepository.getCountPlaylistsToImport(
                PlaylistCountRequest(
                    clientReceiverId = user!!.id,
                    clientWithPlaylistsId = idUserWithData!!
                )
            )

            if (songsIds.isNullOrEmpty() && countPlaylistToImport == 0) {
                updateExceptionResMessage(R.string.fragment_device_infos_no_songs_found_to_download_with_id)
                return
            }

            this.songsIds = songsIds!!
            this.countPlaylistToImport = countPlaylistToImport

            _showDialogConfirmImportData.postValue(AtomicBoolean(true))
        } catch (e: Exception) {
            updateExceptionMessage(e.message)
        }
    }

    /**
     * Updates de liveData to clears the value after actually
     * showing the dialog.
     */
    fun updateShowDialogConfirmImportData(value: Boolean) {
        _showDialogConfirmImportData.postValue(AtomicBoolean(value))
    }
}