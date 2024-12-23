package com.example.keepnote.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.keepnote.entity.Category

@Dao
interface CategoryDao {

    // Menambahkan kategori baru atau mengganti yang lama jika sudah ada
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long // Mengembalikan ID yang dihasilkan

    @Query("SELECT * FROM Categories")
    fun getAllCategories(): LiveData<List<Category>>

    // Menghapus kategori berdasarkan nama
    @Query("DELETE FROM categories WHERE name = :categoryName")
    suspend fun deleteCategoryByName(categoryName: String)

    // Mengambil semua kategori dan mengurutkannya berdasarkan nama
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAll(): LiveData<List<Category>>

    // Mengambil hanya nama kategori dalam urutan alfabet
    @Query("SELECT name FROM categories ORDER BY name ASC")
    fun getAllCategoryNames(): LiveData<List<String>>

    // Mengambil kategori berdasarkan nama (akan mengembalikan null jika tidak ditemukan)
    @Query("SELECT * FROM categories WHERE name = :categoryName LIMIT 1")
    suspend fun getCategoryByName(categoryName: String): Category?
}