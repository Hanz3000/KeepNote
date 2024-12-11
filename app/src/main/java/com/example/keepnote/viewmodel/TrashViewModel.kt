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

    // LiveData untuk mengambil semua data yang ada di trash
    val allTrash: LiveData<List<Trash>> = trashDao.getAllTrash()

    // Referensi Firebase
    private val trashRef = FirebaseDatabase.getInstance().getReference("trash")
    private val noteRef = FirebaseDatabase.getInstance().getReference("notes")
    private val deletedNotesRef = FirebaseDatabase.getInstance().getReference("deleted_notes")
    private val deleteNoteRef = FirebaseDatabase.getInstance().getReference("delete_note_permanent")  // Nama tabel diubah menjadi delete_note_permanent

    // Fungsi untuk memulihkan catatan dari trash ke notes
    fun recover(trash: Trash) {
        viewModelScope.launch(Dispatchers.IO) {
            // Memeriksa apakah catatan sudah ada di dalam notes sebelum melakukan pemulihan
            val existingNote = noteDao.getNoteById(trash.noteId) // Cari berdasarkan noteId atau ID unik lainnya
            if (existingNote == null) {
                // Jika tidak ada, memulihkan catatan ke tabel notes di Room
                val recoveredNote = Note(
                    id = 0, // Biarkan Room generate ID baru
                    title = trash.title,
                    content = trash.content,
                    category = trash.category,
                    timestamp = System.currentTimeMillis().toString() // Pastikan memberikan timestamp
                )

                // Memasukkan catatan yang dipulihkan ke Room
                noteDao.insert(recoveredNote)

                // Memulihkan catatan ke Firebase notes
                noteRef.child(recoveredNote.id.toString()).setValue(recoveredNote)

                // Menghapus catatan dari tabel trash di Room
                trashDao.deleteById(trash.id)

                // Menghapus catatan dari Firebase trash
                trashRef.child(trash.id.toString()).removeValue()
            } else {
                Log.d("TrashViewModel", "Note already exists in notes. Skipping recovery.")
            }
        }
    }

    // Fungsi untuk menghapus catatan secara permanen
    fun permanentlyDelete(trash: Trash) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Logging untuk memantau apakah operasi berhasil
                Log.d("TrashViewModel", "Deleting from Firebase... Trash ID: ${trash.id}")

                // Menghapus dari trash Firebase
                trashRef.child(trash.id.toString()).removeValue().await()
                Log.d("TrashViewModel", "Deleted from trash in Firebase")

                // Menghapus dari deleted_notes Firebase
                deletedNotesRef.child(trash.id.toString()).removeValue().await()
                Log.d("TrashViewModel", "Deleted from deleted_notes in Firebase")

                // Memindahkan ke delete_note_permanent Firebase
                val permanentNote = Trash(
                    id = trash.id,
                    title = trash.title,
                    content = trash.content,
                    category = trash.category,
                    deletedDate = trash.deletedDate,
                    noteId = trash.noteId
                )
                deleteNoteRef.child(trash.id.toString()).setValue(permanentNote).await()
                Log.d("TrashViewModel", "Moved to delete_note_permanent in Firebase")

                // Hapus dari Room database secara permanen
                trashDao.permanentlyDelete(trash.id)

            } catch (e: Exception) {
                Log.e("TrashViewModel", "Error during permanent delete: ${e.message}")
            }
        }
    }


}
