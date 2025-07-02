package br.com.listennow.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Playlist(
    @PrimaryKey
    @NonNull
    val playlistId: String,

    @NonNull
    val name: String
)