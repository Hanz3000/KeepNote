package com.example.keepnote.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Query("DELETE FROM categories WHERE name = :categoryName")
    suspend fun deleteByName(categoryName: String)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): LiveData<List<Category>>

    // Menambahkan fungsi sinkron untuk mengambil kategori
    @Query("SELECT name FROM categories ORDER BY name ASC")
    suspend fun getAllCategoriesSync(): List<String>

    @Query("SELECT * FROM categories")
    fun getAll(): Array<Category>
}
