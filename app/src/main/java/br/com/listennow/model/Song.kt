package br.com.listennow.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.listennow.model.interfaces.IModelKey
import java.io.Serializable

@Entity
data class Song (
    @PrimaryKey
    val videoId: String,
    val name: String,
    val artist: String,
    val album: String,
    val thumb: String,
    var path: String,
    val lyrics: String,
    val requestAt: Long? = null,

    @ColumnInfo(defaultValue = "0")
    val timesPlayed: Int = 0,

    @ColumnInfo(defaultValue = "0")
    val pendingTimesPlayed: Int = 0
): Serializable, IModelKey {
    override fun getModelKey(): String {
        return videoId
    }

    fun hasLyrics(): Boolean {
        return lyrics != "Lyrics Not Found"
    }
}