package com.example.keepnote.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.CategoryDao
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao
import com.example.keepnote.entity.Note
import com.example.keepnote.entity.Trash
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteDao: NoteDao,
    private val categoryDao: CategoryDao,
    private val trashDao: TrashDao
) : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val notesRef = database.getReference("notes")

    // LiveData untuk mendapatkan semua catatan
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    // Fungsi untuk mendapatkan catatan berdasarkan kategori
    fun getNotesByCategory(categoryName: String): LiveData<List<Note>> {
        return noteDao.getNotesByCategory(categoryName)
    }

    // Fungsi untuk menambah catatan
    fun insert(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                note.id = System.currentTimeMillis() // Menetapkan ID unik berdasarkan waktu
            }
            noteDao.insert(note)
            saveNoteToFirebase(note)
        }
    }

    // Fungsi untuk memperbarui catatan
    fun update(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.updateNote(note.id, note.title, note.content, note.category)
            saveNoteToFirebase(note)
        }
    }

    // Fungsi untuk menyimpan atau memperbarui catatan di Firebase
    private fun saveNoteToFirebase(note: Note) {
        notesRef.child(note.id.toString()).setValue(note)
            .addOnSuccessListener {
                Log.d("Firebase", "Note with ID ${note.id} saved/updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to save/update note with ID ${note.id}: ${e.message}")
            }
    }

    // Fungsi untuk mendapatkan semua kategori dalam bentuk nama
    fun getAllCategoryNames(): LiveData<List<String>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.name }  // Mengambil nama dari setiap kategori
        }
    }

    // Fungsi untuk menghapus catatan dan menyimpannya ke Trash
    fun delete(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            trashDao.insert(
                Trash(
                    noteId = note.id,
                    deletedDate = System.currentTimeMillis(),
                    title = note.title,
                    content = note.content,
                    category = note.category,
                )
            )
            noteDao.deleteById(note.id)
            deleteNoteFromFirebase(note.id)
        }
    }

    // Fungsi untuk menghapus catatan dari Firebase
    private fun deleteNoteFromFirebase(noteId: Long) {
        notesRef.child(noteId.toString()).removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Note with ID $noteId deleted successfully from Firebase.")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete note with ID $noteId: ${e.message}")
            }
    }

    // Fungsi untuk menghapus kategori berdasarkan nama
    fun deleteCategoryByName(categoryName: String) {
        viewModelScope.launch {
            categoryDao.deleteCategoryByName(categoryName)
        }
    }

    fun searchNotes(query: String): LiveData<List<Note>> {
        return noteDao.searchNotes("%$query%")
    }

}
