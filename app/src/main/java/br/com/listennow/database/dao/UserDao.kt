package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.listennow.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User LIMIT 1")
    suspend fun existsUserInDevice(): User?

    @Insert
    suspend fun save(user: User)
}