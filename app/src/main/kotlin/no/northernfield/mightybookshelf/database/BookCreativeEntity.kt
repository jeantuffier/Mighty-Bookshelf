package no.northernfield.mightybookshelf.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["bookId", "creativeId"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CreativeEntity::class,
            parentColumns = ["id"],
            childColumns = ["creativeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bookId", "creativeId"]),
    ],
)
data class BookCreativeEntity(
    val bookId: Long,
    val creativeId: Long,
)