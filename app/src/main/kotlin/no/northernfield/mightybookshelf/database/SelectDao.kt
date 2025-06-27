package no.northernfield.mightybookshelf.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

data class BookWithCreatives(
    @ColumnInfo("book_id") val bookId: Long,
    val title: String,
    val type: String,
    val reward: String?,
    val quote: String?,
    val publisher: String,
    val language: String,
    val imageUri: String,
    @ColumnInfo("creative_id") val creativeId: Long,
    val name: String,
    val roles: String,
)

@Dao
interface SelectDao {

    @Query("""
        SELECT 
            book.id as book_id, 
            book.title, 
            book.type, 
            book.reward, 
            book.quote, 
            book.publisher, 
            book.language, 
            book.imageUri, 
            creative.id as creative_id, 
            creative.name,
            creative.roles
        FROM book 
        INNER JOIN book_creative ON book.id = book_creative.book_id
        INNER JOIN creative ON creative.id = book_creative.creative_id
    """)
    fun selectAllBooks(): List<BookWithCreatives>
}