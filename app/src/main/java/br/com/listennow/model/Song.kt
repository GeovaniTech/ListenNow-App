package br.com.listennow.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Song (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val videoId: String,
    val name: String,
    val artist: String,
    val album: String,
    val smallThumb: String,
    val largeThumb: String,
    val smallThumbBytes: ByteArray,
    val largeThumbBytes: ByteArray,
    val path: String,
    val lyrics: String
) : Serializable