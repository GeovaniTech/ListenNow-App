package br.com.listennow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val email: String,
    val password: String
)