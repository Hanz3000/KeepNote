package com.example.keepnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.keepnote.dao.CategoryDao

// Kelas ini adalah factory untuk membuat instance CategoryViewModel
class CategoryViewModelFactory(private val categoryDao: CategoryDao) : ViewModelProvider.Factory {

    // Fungsi untuk membuat ViewModel baru
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Memeriksa apakah modelClass adalah CategoryViewModel
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(categoryDao) as T // Mengembalikan instance CategoryViewModel
        }
        // Jika modelClass tidak diketahui, lempar IllegalArgumentException
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
