package br.com.listennow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.database.dao.SongDao
import br.com.listennow.database.dao.UserDao
import br.com.listennow.migrations.MIGRATION_5_6
import br.com.listennow.migrations.MIGRATION_6_7
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong
import br.com.listennow.model.Song
import br.com.listennow.model.User

@Database(entities = [Song::class, User::class, Playlist::class, PlaylistSong::class], version = 7, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        private const val DATABASE_NAME = "listennow.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                    .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
                    .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}