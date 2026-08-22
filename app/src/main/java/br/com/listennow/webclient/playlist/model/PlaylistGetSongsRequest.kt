package br.com.listennow.webclient.playlist.model

data class PlaylistGetSongsRequest(
    val playlistId: String,
    val ignoreIds: List<String>?
)