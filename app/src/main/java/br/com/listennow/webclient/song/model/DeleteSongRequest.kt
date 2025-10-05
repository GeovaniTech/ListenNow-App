package br.com.listennow.webclient.song.model
data class DeleteSongRequest(
    val videoId: String,
    val clientId: String
)