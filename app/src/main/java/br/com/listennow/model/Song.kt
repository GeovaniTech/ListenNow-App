package br.com.listennow.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(videoId)
        parcel.writeString(name)
        parcel.writeString(artist)
        parcel.writeString(album)
        parcel.writeString(smallThumb)
        parcel.writeString(largeThumb)
        parcel.writeString(path)
        parcel.writeString(lyrics)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}