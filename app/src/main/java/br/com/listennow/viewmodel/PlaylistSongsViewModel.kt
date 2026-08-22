package br.com.listennow.viewmodel

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.listennow.R
import br.com.listennow.foreground.DownloadYoutubeSongService
import br.com.listennow.foreground.DownloadYoutubeSongsActions
import br.com.listennow.model.Song
import br.com.listennow.navparams.PlaylistSongsNavParams
import br.com.listennow.repository.PlaylistRepository
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import br.com.listennow.webclient.playlist.model.SongPlaylistResponse
import br.com.listennow.webclient.song.model.SearchYTSongResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PlaylistSongsViewModel @Inject constructor(
    userRepository: UserRepository,
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository
): CommonViewModel(userRepository) {
    lateinit var navParams: PlaylistSongsNavParams
    var query: String? = null

    private var _songs: MutableLiveData<List<Song>> = MutableLiveData()
    val songs: LiveData<List<Song>> get() = _songs

    private var _onSongsAddedCallback: MutableLiveData<AtomicBoolean> = MutableLiveData(AtomicBoolean(false))
    val onSongsAddedCallback: LiveData<AtomicBoolean> get() = _onSongsAddedCallback

    private var _onSongsDeletedCallback: MutableLiveData<AtomicBoolean> = MutableLiveData(AtomicBoolean(false))
    val onSongsDeletedCallback: LiveData<AtomicBoolean> get() = _onSongsDeletedCallback

    private var _syncing: MutableLiveData<AtomicBoolean> = MutableLiveData(AtomicBoolean(false))
    val syncing: LiveData<AtomicBoolean> get() = _syncing

    private var _songToImport: MutableLiveData<List<SongPlaylistResponse?>?> = MutableLiveData(null)
    val songToImport: LiveData<List<SongPlaylistResponse?>?> get() = _songToImport

    fun loadData() = viewModelScope.launch {
        _songs.postValue(songRepository.getSongsFromPlaylist(navParams.playlistId, query))
    }

    fun addSongsToPlaylist(songsIds: ArrayList<String>?) = viewModelScope.launch {
        songsIds?.let { songs ->
            playlistRepository.insertSongsIntoPlaylist(
                playlistId = navParams.playlistId,
                songs = songs
            )
            postSongsAddedCallback(true)
        }
    }

    fun postSongsAddedCallback(value: Boolean) {
        _onSongsAddedCallback.postValue(AtomicBoolean(value))
    }

    fun postSongsDeletedCallback(value: Boolean) {
        _onSongsDeletedCallback.postValue(AtomicBoolean(value))
    }

    suspend fun getSongsIdsFromPlaylist(): List<String> {
        return songRepository.getSongsFromPlaylist(navParams.playlistId, null).map { it.videoId }
    }

    fun deleteSong(item: Song) = viewModelScope.launch {
        playlistRepository.deleteSongsFromPlaylist(navParams.playlistId, listOf(item.videoId))
        _onSongsDeletedCallback.postValue(AtomicBoolean(true))
    }

    fun refreshPlaylistSongs() {
        viewModelScope.launch {
            val songsOnDevice = songRepository.getSongsFromPlaylist(
                playlistId = navParams.playlistId,
                query = null
            ).map { it.videoId }

            val songsOnServer = playlistRepository.getPlaylistSongsOnServer(
                playlistId = navParams.playlistId,
                ignoreIds = songsOnDevice
            )

            val newSongs =  songsOnServer?.filter { serverSong ->
                serverSong.videoId !in songsOnDevice
            }

            _songToImport.postValue(newSongs)

            if (newSongs.isNullOrEmpty()) {
                updateExceptionResMessage(R.string.no_songs_found_to_import)
            } else {
                updateExceptionResMessage(R.string.your_songs_are_being_imported)
            }

            updateSyncingState(false)
        }
    }

    fun updateSyncingState(isSyncing: Boolean) {
        _syncing.postValue(AtomicBoolean(isSyncing))
    }
}