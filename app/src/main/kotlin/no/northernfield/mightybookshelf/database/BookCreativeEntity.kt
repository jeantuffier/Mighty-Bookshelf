package no.northernfield.mightybookshelf.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.serialization.SerialName

@Entity(
    tableName = "book_creative",
    primaryKeys = ["book_id", "creative_id"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CreativeEntity::class,
            parentColumns = ["id"],
            childColumns = ["creative_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["book_id", "creative_id"]),
    ],
)
data class BookCreativeEntity(
    @ColumnInfo(name = "book_id") val bookId: Long,
    @ColumnInfo(name = "creative_id") val creativeId: Long,
)