package br.com.listennow.webclient.song.model

data class SongTimesPlayedRestModel (
    val videoId: String,
    val timesToIncrease: Int
)