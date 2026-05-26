package br.com.listennow.webclient.playlist.model

data class PlaylistCreateRequest(
    val playlistName: String,
    val clientId: String
)