package br.com.listennow.webclient.playlist.model

data class PlaylistDeleteSongsRequest(
    val playlistId: String,
    val songs: List<String>
)