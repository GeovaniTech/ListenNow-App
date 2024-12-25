package br.com.listennow.webclient.song.model

data class SongIdsRequest(
    val userReceiver: String,
    val userWithSongs: String
)