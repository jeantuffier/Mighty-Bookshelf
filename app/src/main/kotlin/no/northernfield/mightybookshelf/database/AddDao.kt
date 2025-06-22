package no.northernfield.mightybookshelf.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface AddDao {

    @Insert
    fun insertBook(book: BookEntity): Long

    @Insert
    fun insertCreative(creative: CreativeEntity): Long

    @Insert
    fun insertBookCreative(bookCreative: BookCreativeEntity)
}