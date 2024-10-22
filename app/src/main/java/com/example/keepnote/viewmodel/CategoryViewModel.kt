package com.example.keepnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.entity.Category
import kotlinx.coroutines.launch

class CategoryViewModel(private val categoryDao: CategoryDao) : ViewModel() {

    // Mengambil semua kategori dari database
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    // Fungsi untuk menambahkan kategori baru
    fun insert(category: Category) {
        viewModelScope.launch {
            categoryDao.insert(category)
        }
    }

    // Fungsi untuk menghapus kategori
    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            categoryDao.deleteByName(categoryName)
        }
    }
}