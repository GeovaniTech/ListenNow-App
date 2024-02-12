package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.listennow.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE email = :email AND password = :password")
    fun authenticateUser(email: String, password: String): Flow<User?>

    @Query("SELECT * FROM User WHERE email = :email")
    fun existsAccount(email: String): User?

    @Insert
    fun save(user: User)

    @Query("SELECT * FROM User WHERE id = :id")
    fun findById(id: Long): Flow<User?>
}