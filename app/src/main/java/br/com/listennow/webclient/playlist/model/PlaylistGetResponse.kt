package br.com.listennow.webclient.playlist.model

data class PlaylistGetResponse(
    val id: String,
    val name: String,
    val songs: List<String>
)