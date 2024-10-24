package com.example.keepnote

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao
import com.example.keepnote.entity.Category
import com.example.keepnote.entity.Note
import com.example.keepnote.entity.Trash
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var categoryDao: CategoryDao // DAO untuk tabel Category
    private lateinit var noteDao: NoteDao // DAO untuk tabel Note
    private lateinit var trashDao: TrashDao // DAO untuk tabel Trash
    private lateinit var db: AppDatabase // Objek database Room

    // Objek contoh untuk tes
    private val category = Category(1, "Belajar")
    private val note = Note(1, "Mobile", "Kotlin", "Belajar", "2024-10-21 15:52:00")
    private val trash = Trash(1, 1, 21-10-2024, "Mobile", "Kotlin", "Belajar")

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext() // Mendapatkan konteks aplikasi
        // Membuat database Room di memori
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        //Menginisialisasi objek DAO
        categoryDao = db.categoryDao()
        noteDao = db.noteDao()
        trashDao = db.trashDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() = db.close() // Menutup database setelah selesai digunakan

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveCategory(){
        categoryDao.insert(category) // Menyisipkan kategori ke dalam tabel
        val result = categoryDao.getAll() // Mengambil semua kategori dari tabel
        assert(result.size == 1) // Memastikan bahwa ukuran data yang diterima sama dengan 1
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveNote(){
        noteDao.insert(note) // Menyisipkan catatan ke dalam tabel
        val result = noteDao.getAll() // Mengambil semua catatan dari tabel
        assert(result.size == 1) // Memastikan bahwa ukuran data yang diterima sama dengan 1
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveTrash(){
        trashDao.insert(trash) // Menyisipkan data sampah ke dalam tabel
        val result = trashDao.getAll() // Mengambil semua data sampah dari tabel
        assert(result.size == 1) // Memastikan bahwa ukuran data yang diterima sama dengan 1
    }
}