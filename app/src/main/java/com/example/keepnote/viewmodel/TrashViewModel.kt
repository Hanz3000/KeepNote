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
            // Pindahkan catatan ke delete_note_permanent sebelum dihapus permanen
            val permanentNote = Trash(
                id = trash.id,
                title = trash.title,
                content = trash.content,
                category = trash.category,
                deletedDate = trash.deletedDate, // Pastikan memberi nilai deletedDate
                noteId = trash.noteId // Pastikan memberi nilai noteId
            )

            // Pindahkan catatan ke delete_note_permanent di Firebase
            try {
                // Lakukan pemindahan ke delete_note_permanent
                val task1 = deleteNoteRef.child(trash.id.toString()).setValue(permanentNote).await()

                // Jika berhasil memindahkan ke delete_note_permanent, hapus catatan dari deleted_notes
                val task2 = deletedNotesRef.child(trash.id.toString()).removeValue().await()

                // Hapus catatan dari Room Database secara permanen
                trashDao.permanentlyDelete(trash.id)

                // Hapus catatan dari Firebase trash
                trashRef.child(trash.id.toString()).removeValue()

            } catch (e: Exception) {
                // Tangani error
                Log.e("TrashViewModel", "Error during permanent delete: ${e.message}")
            }
        }
    }
}
