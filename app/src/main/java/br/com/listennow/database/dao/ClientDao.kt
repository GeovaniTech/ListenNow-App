package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.listennow.model.Client

@Dao
interface ClientDao {
    @Query("SELECT * FROM Client WHERE email = :email AND password = :password")
    fun authenticateClient(email: String, password: String): Client

    @Query("SELECT * FROM Client WHERE email = :email")
    fun existsAccount(email: String): Client

    @Insert
    fun save(client: Client)
}