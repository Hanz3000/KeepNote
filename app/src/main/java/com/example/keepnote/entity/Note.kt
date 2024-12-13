package com.example.keepnote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Mendefinisikan entitas Note yang mewakili tabel "notes" di database
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var title: String,
    var content: String,
    var category: String,
    var timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
) {
    companion object {
        fun getCurrentTimestamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        }
    }
}

