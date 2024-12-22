package com.example.keepnote.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.entity.Category
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel untuk mengelola data kategori
class CategoryViewModel(private val categoryDao: CategoryDao) : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference("categories")

    // Mengambil semua kategori dari database sebagai LiveData
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    // Fungsi untuk menambahkan kategori baru
    fun insert(category: Category) {
        // Meluncurkan coroutine untuk menjalankan operasi database di latar belakang
        viewModelScope.launch(Dispatchers.IO) {
            categoryDao.insert(category) // Memanggil fungsi insert dari CategoryDao
            categoriesRef.child(category.name).setValue(category)
        }
    }

}