package br.com.listennow.webclient.song.model

data class IncreaseSongTimesPlayedRequest(
    val clientId: String,
    val songs: List<SongTimesPlayedRestModel>
)