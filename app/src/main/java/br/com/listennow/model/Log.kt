package br.com.listennow.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.listennow.model.interfaces.IModelKey
import java.util.Date
import java.util.UUID

@Entity
data class Log (
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val level: String,
    val tag: String,
    val message: String,
    val createAt: Long = Date().time,
    var isSynced: Boolean = false
): IModelKey {
    override fun getModelKey(): String {
        return id
    }
}