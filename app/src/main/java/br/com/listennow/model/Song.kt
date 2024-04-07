package br.com.listennow.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Song (
    @PrimaryKey
    val songId: String = UUID.randomUUID().toString(),
    val videoId: String,
    val name: String,
    val artist: String,
    val album: String,
    val smallThumb: String,
    val largeThumb: String,
    var path: String,
    val lyrics: String
)