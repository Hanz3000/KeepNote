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
        viewModelScope.launch(Dispatchers.IO) {
            // Menyimpan data ke database lokal dan mendapatkan ID yang dihasilkan
            val generatedId = categoryDao.insert(category)

            // Membuat objek data dengan ID dari database lokal
            val categoryData = mapOf(
                "id" to generatedId,
                "name" to category.name
            )

            // Menyimpan data ke Firebase menggunakan ID sebagai kunci utama
            categoriesRef.child(generatedId.toString()).setValue(categoryData)
                .addOnSuccessListener {
                    // Data berhasil disimpan di Firebase
                    println("Kategori berhasil disimpan ke Firebase dengan ID $generatedId")
                }
                .addOnFailureListener {
                    // Tangani kesalahan jika penyimpanan ke Firebase gagal
                    it.printStackTrace()
                }
        }
    }



}