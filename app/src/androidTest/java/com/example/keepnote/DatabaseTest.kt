package com.example.keepnote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.entity.Category
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var categoryDao: CategoryDao // DAO untuk tabel Category
    private lateinit var db: AppDatabase // Objek database Room

    // Objek contoh untuk tes
    private val category = Category(1, "Belajar")

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext() // Mendapatkan konteks aplikasi
        // Membuat database Room di memori
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        // Menginisialisasi objek DAO
        categoryDao = db.categoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() = db.close() // Menutup database setelah selesai digunakan

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveCategory() {
        categoryDao.insert(category) // Memasukkan kategori ke dalam database
        val result = categoryDao.getAll() // Mengambil semua kategori dari tabel

        assert(result.size == 1) // Memastikan bahwa ada satu kategori yang disimpan
        assert(result[0] == category) // Memastikan kategori yang diambil sesuai dengan kategori yang dimasukkan
    }

    @Test
    @Throws(Exception::class)
    fun deleteCategoryByName() {
        categoryDao.insert(category) // Menyisipkan kategori ke dalam tabel
        categoryDao.deleteByName(category.name) // Menghapus kategori berdasarkan nama

        val result = categoryDao.getAll() // Mengambil semua kategori dari tabel
        assert(result.isEmpty()) // Memastikan bahwa tabel kosong setelah penghapusan
        //farhan boss
    }
}
