package com.pointpadel.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Partido::class],
    version = 1,
    exportSchema = false
)
abstract class PadelDatabase : RoomDatabase() {

    abstract fun partidoDao(): PartidoDao

    companion object {
        const val DATABASE_NAME = "padel_database"
    }
}
