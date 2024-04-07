package br.com.listennow.webclient.song.model

data class SearchYTSongResponse(
    val videoId: String,
    val title: String,
    val artist: String,
    val thumb: String
)