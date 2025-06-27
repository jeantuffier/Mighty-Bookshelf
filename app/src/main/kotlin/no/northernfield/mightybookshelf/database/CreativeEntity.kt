package no.northernfield.mightybookshelf.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import no.northernfield.mightybookshelf.add.CreativeRoles

@Entity(tableName = "creative")
data class CreativeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val roles: CreativeRoles,
)