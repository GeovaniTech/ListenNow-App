package br.com.listennow.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.listennow.model.Log

@Dao
interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(log: Log)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(logs: List<Log>)

    @Query("SELECT * FROM Log WHERE isSynced = 0 ")
    suspend fun getPendingLogs(): List<Log>
}