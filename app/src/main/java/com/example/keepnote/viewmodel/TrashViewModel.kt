package com.example.keepnote.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepnote.dao.NoteDao
import com.example.keepnote.dao.TrashDao
import com.example.keepnote.entity.Note
import com.example.keepnote.entity.Trash
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TrashViewModel(private val trashDao: TrashDao, private val noteDao: NoteDao) : ViewModel() {

    val allTrash: LiveData<List<Trash>> = trashDao.getAllTrash()
    private val noteRef = FirebaseDatabase.getInstance().getReference("notes")
    private val deletedNotesRef = FirebaseDatabase.getInstance().getReference("deleted_notes")

    fun recover(trash: Trash) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val recoveredNote = Note(
                    id = trash.id,
                    title = trash.title,
                    content = trash.content,
                    category = trash.category,
                    timestamp = System.currentTimeMillis().toString()
                )
                noteDao.insert(recoveredNote)
                trashDao.deleteById(trash.id)
                deletedNotesRef.child(trash.id.toString()).removeValue()
                noteRef.child(trash.id.toString()).setValue(recoveredNote)
            } catch (e: Exception) {
                Log.e("TrashViewModel", "Error recovering note: ${e.message}")
            }
        }
    }

    fun permanentlyDelete(trash: Trash) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deletedNotesRef.child(trash.id.toString()).removeValue()
                    .addOnSuccessListener {
                        Log.d("TrashViewModel", "Catatan dengan ID ${trash.id} berhasil dihapus dari Firebase.")

                        // Hapus dari Room database secara permanen setelah berhasil menghapus dari Firebase
                        viewModelScope.launch(Dispatchers.IO) {
                            trashDao.permanentlyDelete(trash.id)
                            Log.d("TrashViewModel", "Catatan dengan ID ${trash.id} berhasil dihapus dari Room database secara permanen.")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("TrashViewModel", "Gagal menghapus catatan dari Firebase: ${exception.message}")
                    }
            } catch (e: Exception) {
                Log.e("TrashViewModel", "Error during permanent delete: ${e.message}")
            }
        }
    }
}