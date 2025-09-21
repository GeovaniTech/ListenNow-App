package br.com.listennow.model

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
    val lyrics: String
): Serializable, IModelKey {
    override fun getModelKey(): String {
        return videoId
    }
}