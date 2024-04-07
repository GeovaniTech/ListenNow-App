package br.com.listennow.webclient.song.model

data class SearchDownloadSongRequest(
    val video_id: String,
    val client_id: String
)