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
    private lateinit var categoryDao: CategoryDao
    private lateinit var noteDao: NoteDao
    private lateinit var trashDao: TrashDao
    private lateinit var db: AppDatabase

    private val category = Category(1, "Belajar")
    private val note = Note(1, "Mobile", "Kotlin", "Belajar", "2024-10-21 15:52:00")
    private val trash = Trash(1, 1, 21-10-2024, "Mobile", "Kotlin", "Belajar")

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        categoryDao = db.categoryDao()
        noteDao = db.noteDao()
        trashDao = db.trashDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() = db.close()

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveCategory(){
        categoryDao.insert(category)
        val result = categoryDao.getAll()
        assert(result.size == 1)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveNote(){
        noteDao.insert(note)
        val result = noteDao.getAll()
        assert(result.size == 1)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndRetrieveTrash(){
        trashDao.insert(trash)
        val result = trashDao.getAll()
        assert(result.size == 1)
    }
}