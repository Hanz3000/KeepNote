package com.example.keepnote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Defining the Category entity that represents the "categories" table in the database
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) var id: Long = 0, // Primary Key, auto-generated
    val name: String // Column to store the category name
)
