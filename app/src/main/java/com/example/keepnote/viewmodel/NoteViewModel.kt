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
import com.example.keepnote.entity.Category
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

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    fun getNotesByCategory(categoryName: String): LiveData<List<Note>> {
        return noteDao.getNotesByCategory(categoryName)
    }

    fun insert(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            if (note.id == 0L) {
                note.id = System.currentTimeMillis()
            }

            noteDao.insert(note)
            saveNoteToFirebase(note)
        }
    }

    fun update(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.updateNote(note.id, note.title, note.content, note.category)
            saveNoteToFirebase(note)
        }
    }

    private fun saveNoteToFirebase(note: Note) {
        notesRef.child(note.id.toString()).setValue(note)
            .addOnSuccessListener {
                Log.d("Firebase", "Note with ID ${note.id} saved/updated successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to save/update note with ID ${note.id}: ${e.message}")
            }
    }

    // Menampilkan semua kategori dalam bentuk nama
    fun getAllCategoryNames(): LiveData<List<String>> {
        return categoryDao.getAllCategories().map { categories ->
            categories.map { it.name }  // Mengambil nama dari setiap kategori
        }
    }

    // Menampilkan catatan yang dihapus di halaman Trash
    val allTrashNotes: LiveData<List<Trash>> = trashDao.getAllTrashNotes()

    // Menghapus catatan dan menyimpannya ke Trash
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

    private fun deleteNoteFromFirebase(noteId: Long) {
        notesRef.child(noteId.toString()).removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Note with ID $noteId deleted successfully from Firebase.")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete note with ID $noteId: ${e.message}")
            }
    }

    // Menghapus kategori berdasarkan nama
    fun deleteCategoryByName(categoryName: String) {
        viewModelScope.launch {
            categoryDao.deleteCategoryByName(categoryName)
        }
    }

    // Memperbarui kategori pada catatan tertentu
    fun updateCategoryForNotes(oldCategory: String, newCategory: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldCategoryObj = categoryDao.getCategoryByName(oldCategory)
            val newCategoryObj = categoryDao.getCategoryByName(newCategory)

            if (oldCategoryObj != null && newCategoryObj != null) {
                noteDao.updateCategoryForNotes(oldCategoryObj.name, newCategoryObj.name)
            }
        }
    }

    // Menambahkan kategori baru
    fun insertCategory(categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val category = Category(name = categoryName)
            categoryDao.insert(category)
        }
    }

    // Memulihkan catatan dari Trash ke halaman Note
    fun restoreNoteFromTrash(trash: Trash) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = Note(
                id = trash.noteId,
                title = trash.title,
                content = trash.content,
                category = trash.category,
            )

            noteDao.insert(note)
            trashDao.deleteById(trash.noteId)
        }
    }

    // Menghapus catatan secara permanen dari Trash
    fun deleteNotePermanentlyFromTrash(trashId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            trashDao.permanentlyDelete(trashId)
        }
    }
}
