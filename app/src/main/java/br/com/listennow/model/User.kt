package br.com.listennow.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class User (
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val password: String
)