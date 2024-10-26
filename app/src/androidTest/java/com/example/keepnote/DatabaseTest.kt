package com.example.keepnote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.entity.Category
import com.example.keepnote.entity.Note
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var categoryDao: CategoryDao // DAO untuk tabel Category
    private lateinit var db: AppDatabase // Objek database Room
    private lateinit var noteDao: NoteDao // DAO untuk tabel Note


    // Objek contoh untuk tes
    private val category = Category(1, "Belajar")
    private val note = Note(1, "Mobile", "Kotlin", "Belajar", "2024-10-21 15:52:00")



    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext() // Mendapatkan konteks aplikasi
        // Membuat database Room di memori
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        // Menginisialisasi objek DAO
        categoryDao = db.categoryDao()
        noteDao = db.noteDao()

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

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveNote(){
        noteDao.insert(note) // Menyisipkan catatan ke dalam tabel
        val result = noteDao.getAll() // Mengambil semua catatan dari tabel
        assert(result.size == 1) // Memastikan bahwa ukuran data yang diterima sama dengan 1
        }
    // Novan


    //Diva
    @Test
    @kotlin.jvm.Throws(Exception::class)
    fun deletebyId() {
        noteDao.insert(note)
        noteDao.deleteById(note.id)
        val result = noteDao.getAll()
        assert(result.isEmpty())
    }
}
