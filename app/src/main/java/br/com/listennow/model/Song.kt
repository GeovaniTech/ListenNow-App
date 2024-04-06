package br.com.listennow.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID

@Entity
data class Song (
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val videoId: String,
    val name: String,
    val artist: String,
    val album: String,
    val smallThumb: String,
    val largeThumb: String,
    var path: String,
    val lyrics: String,
) : Serializable