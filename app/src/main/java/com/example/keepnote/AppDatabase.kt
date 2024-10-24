package com.example.keepnote

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao
import com.example.keepnote.entity.Category
import com.example.keepnote.entity.Note
import com.example.keepnote.entity.Trash

@Database(entities = [Note::class, Category::class, Trash::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao // Mengembalikan DAO untuk catatan
    abstract fun categoryDao(): CategoryDao // Mengembalikan DAO untuk kategori
    abstract fun trashDao(): TrashDao // Mengembalikan DAO untuk trash

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null // Instance database yang akan digunakan

        fun getDatabase(context: Context): AppDatabase {
            // Menggunakan INSTANCE untuk memeriksa apakah sudah ada instance yang ada
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keepnote_database" // Nama database
                ).build()
                INSTANCE = instance // Menyimpan instance ke dalam INSTANCE
                instance
            }
        }
    }
}
