package com.example.keepnote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trash")
data class Trash(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,
    val deletedDate: Long,
    val title: String,
    val content: String,
    val category: String, // Tambahkan field category
    val isPermanentlyDeleted: Boolean = false
)