package br.com.listennow.model

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "videoId"])
data class PlaylistSong(
    val playlistId: String,
    val videoId: String
)