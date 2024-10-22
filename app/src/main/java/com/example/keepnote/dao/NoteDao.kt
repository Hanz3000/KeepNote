package com.example.keepnote.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)


    // Mengembalikan LiveData untuk pemantauan otomatis
    @Query("SELECT * FROM notes ORDER BY id DESC") // Pastikan nama tabel sesuai
    fun getAllNotes(): LiveData<List<Note>> // Mengembalikan LiveData

    @Query("DELETE FROM notes WHERE id = :noteId") // Pastikan nama tabel sesuai
    suspend fun deleteById(noteId: Long)

    @Query("UPDATE notes SET title = :title, content = :content, category = :category WHERE id = :noteId")
    suspend fun updateNote(noteId: Long, title: String, content: String, category: String)

    @Query("SELECT * FROM notes WHERE category = :categoryName ORDER BY id DESC")
    fun getNotesByCategory(categoryName: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE category IS NULL")
    fun getUncategorizedNotes(): LiveData<List<Note>>

    @Query("UPDATE notes SET category = 'Uncategorized' WHERE category = :oldCategory")
    suspend fun updateCategoryToDefault(oldCategory: String)

    @Query("SELECT * FROM notes ORDER BY id DESC") // Pastikan nama tabel sesuai
    fun getAll(): Array<Note>
}