package br.com.listennow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.database.dao.PlaylistSongDao
import br.com.listennow.database.dao.SongDao
import br.com.listennow.database.dao.UserDao
import br.com.listennow.model.Playlist
import br.com.listennow.model.PlaylistSong
import br.com.listennow.model.Song
import br.com.listennow.model.User

@Database(entities = [Song::class, User::class, Playlist::class, PlaylistSong::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun userDao(): UserDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistSongDao(): PlaylistSongDao

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
                    ).fallbackToDestructiveMigration().allowMainThreadQueries()
                    .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}