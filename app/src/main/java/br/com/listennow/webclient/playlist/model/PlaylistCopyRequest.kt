package br.com.listennow.webclient.playlist.model

data class PlaylistCopyRequest(
    val clientReceiverId: String,
    val clientWithPlaylistsId: String
)
