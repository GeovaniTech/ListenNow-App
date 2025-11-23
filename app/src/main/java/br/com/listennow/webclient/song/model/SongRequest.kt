package br.com.listennow.webclient.song.model

data class SongRequest(
    val client_id: String,
    val ignoreIds: List<String>
)