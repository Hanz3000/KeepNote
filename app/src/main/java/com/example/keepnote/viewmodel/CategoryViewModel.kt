package com.example.keepnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.entity.Category
import kotlinx.coroutines.launch

// ViewModel untuk mengelola data kategori
class CategoryViewModel(private val categoryDao: CategoryDao) : ViewModel() {

    // Mengambil semua kategori dari database sebagai LiveData
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    // Fungsi untuk menambahkan kategori baru
    fun insert(category: Category) {
        // Meluncurkan coroutine untuk menjalankan operasi database di latar belakang
        viewModelScope.launch {
            categoryDao.insert(category) // Memanggil fungsi insert dari CategoryDao
        }
    }

    // Fungsi untuk menghapus kategori berdasarkan nama
    fun deleteCategory(categoryName: String) {
        // Meluncurkan coroutine untuk menjalankan operasi database di latar belakang
        viewModelScope.launch {
            categoryDao.deleteByName(categoryName) // Memanggil fungsi deleteByName dari CategoryDao
        }
    }
}
