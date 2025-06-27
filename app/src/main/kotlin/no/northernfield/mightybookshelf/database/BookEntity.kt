package no.northernfield.mightybookshelf.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import no.northernfield.mightybookshelf.add.BookType

@Entity(tableName = "book")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: BookType,
    val reward: String,
    val quote: String,
    val publisher: String,
    val language: String,
    val imageUri: String,
)