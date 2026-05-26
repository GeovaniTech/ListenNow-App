package br.com.listennow.webclient.playlist.model

data class PlaylistInsertSongsRequest(
    val playlistId: String,
    val songs: List<String>
)