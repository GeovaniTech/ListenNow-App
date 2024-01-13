package br.com.listennow.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE Client")
        database.execSQL("CREATE TABLE IF NOT EXISTS `User` (`id` INTEGER NOT NULL, `email` TEXT NOT NULL, `password` TEXT NOT NULL, PRIMARY KEY(`id`))")
    }

}