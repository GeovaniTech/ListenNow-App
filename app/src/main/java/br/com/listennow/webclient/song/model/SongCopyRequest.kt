package br.com.listennow.webclient.song.model

data class SongCopyRequest(
    val userReceiver: String,
    val songsIds: List<String>
)