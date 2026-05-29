package br.com.listennow.webclient.playlist.model

data class PlaylistCountRequest(
    val clientReceiverId: String,
    val clientWithPlaylistsId: String
)