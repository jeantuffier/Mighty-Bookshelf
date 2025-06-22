package no.northernfield.mightybookshelf.database

import androidx.room.Database

@Database(
    entities = [
        BookEntity::class,
        CreativeEntity::class,
        BookCreativeEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : androidx.room.RoomDatabase() {
    abstract fun addDao(): AddDao
}