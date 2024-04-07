package br.com.listennow.model

import androidx.room.Entity

@Entity(primaryKeys = ["songId", "playlistId"])
data class PlaylistSong(
    val songId: String,
    val playlistId: String
)