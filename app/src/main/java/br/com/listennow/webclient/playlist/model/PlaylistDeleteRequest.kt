package br.com.listennow.webclient.playlist.model

data class PlaylistDeleteRequest(
    val playlistId: String,
    val clientId: String
)