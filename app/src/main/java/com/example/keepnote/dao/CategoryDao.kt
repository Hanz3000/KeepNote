package com.example.keepnote.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.keepnote.entity.Category

// Data Access Object (DAO) untuk operasi database pada tabel kategori (categories)
@Dao
interface CategoryDao {

    // Menyisipkan atau memperbarui kategori ke dalam database
    // Jika kategori sudah ada (berdasarkan nama), data akan digantikan karena menggunakan strategi REPLACE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    // Menghapus kategori berdasarkan nama
    @Query("DELETE FROM categories WHERE name = :categoryName")
    suspend fun deleteByName(categoryName: String)

    // Mengambil semua kategori dalam urutan abjad dan mengembalikannya sebagai LiveData
    // LiveData memungkinkan pengamatan pada perubahan data secara real-time
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    // Mengambil semua kategori dan mengembalikannya sebagai array
    // Ini adalah fungsi sinkron yang mengembalikan seluruh isi tabel kategori
    @Query("SELECT * FROM categories")
    fun getAll(): Array<Category>
}
