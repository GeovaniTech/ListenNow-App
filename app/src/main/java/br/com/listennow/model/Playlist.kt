package br.com.listennow.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Playlist(
    @PrimaryKey
    val playlistId: String,
    val name: String
)