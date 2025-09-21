package br.com.listennow.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "videoId",
        associateBy = Junction(PlaylistSong::class)
    )
    val songs: List<Song>
)