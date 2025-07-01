package br.com.listennow.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE `Playlist` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, " +
                "PRIMARY KEY(`id`))")
    }
}
