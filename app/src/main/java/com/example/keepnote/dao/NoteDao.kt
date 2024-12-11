package com.example.keepnote.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.keepnote.entity.Note

// Data Access Object (DAO) untuk operasi database pada tabel notes
@Dao
interface NoteDao {

    // Jika catatan dengan ID yang sama sudah ada, catatan lama akan diganti karena menggunakan strategi REPLACE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    // Mengambil semua catatan dan mengembalikan sebagai LiveData
    // Data akan diurutkan berdasarkan ID secara descending (dari terbaru ke terlama)
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>> // Menggunakan LiveData agar perubahan data otomatis diperbarui di UI

    // Menghapus catatan dari database berdasarkan ID catatan
    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteById(noteId: Long)

    // Memperbarui judul, isi, dan kategori catatan berdasarkan ID
    @Query("UPDATE notes SET title = :title, content = :content, category = :category WHERE id = :noteId")
    suspend fun updateNote(noteId: Long, title: String, content: String, category: String)

    // Mengambil catatan yang sesuai dengan kategori tertentu dan mengembalikannya sebagai LiveData
    // Data akan diurutkan berdasarkan ID secara descending
    @Query("SELECT * FROM notes WHERE category = :categoryName ORDER BY id DESC")
    fun getNotesByCategory(categoryName: String): LiveData<List<Note>>

    // Mengambil semua catatan yang tidak memiliki kategori (NULL) dan mengembalikannya sebagai LiveData
    @Query("SELECT * FROM notes WHERE category IS NULL ORDER BY id DESC")
    fun getUncategorizedNotes(): LiveData<List<Note>>

    // Mengubah kategori catatan yang sesuai dengan kategori lama menjadi 'Uncategorized'
    @Query("UPDATE notes SET category = 'Tidak Ada Kategori' WHERE category = :oldCategory")
    suspend fun updateCategoryToDefault(oldCategory: String)

    // Mengubah kategori catatan yang sesuai dengan kategori lama menjadi kategori baru
    @Query("UPDATE notes SET category = :newCategory WHERE category = :oldCategory")
    suspend fun updateCategoryForNotes(oldCategory: String, newCategory: String)

    // Mengambil semua catatan dan mengembalikannya sebagai array
    // Ini adalah versi sinkron untuk mengambil data tanpa LiveData
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAll(): Array<Note>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Long): Note?

    @Query("SELECT * FROM notes WHERE title LIKE :query OR content LIKE :query")
    fun searchNotes(query: String): LiveData<List<Note>>
}
