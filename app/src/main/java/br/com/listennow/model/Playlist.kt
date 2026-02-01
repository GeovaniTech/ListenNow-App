package br.com.listennow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey
    val playlistId: String,

    val name: String
)