package com.example.keepnote.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.NoteApplication
import com.example.keepnote.R
import com.example.keepnote.adapter.TrashAdapter
import com.example.keepnote.entity.Trash
import com.example.keepnote.viewmodel.TrashViewModel
import com.example.keepnote.viewmodel.TrashViewModelFactory

class TrashActivity : AppCompatActivity() {

    // ViewModel untuk operasi yang terkait dengan trash (sampah)
    private lateinit var trashViewModel: TrashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)
        // Mengambil DAO dari NoteApplication untuk menginisialisasi ViewModel
        val trashDao = (application as NoteApplication).database.trashDao()
        val noteDao = (application as NoteApplication).database.noteDao()
        val factory = TrashViewModelFactory(trashDao, noteDao)

        // Menginisialisasi ViewModel menggunakan Factory
        trashViewModel = viewModels<TrashViewModel> { factory }.value

        // Mengatur RecyclerView dan adapter untuk menampilkan catatan yang ada di trash (sampah)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_trash)
        val adapter = TrashAdapter(
            emptyList(),
            { trash ->
                // Fungsi untuk memulihkan catatan dari trash
                trashViewModel.recover(trash)
                Toast.makeText(this, "Catatan dipulihkan", Toast.LENGTH_SHORT).show()
            },
            { trash ->
                // Menampilkan dialog konfirmasi untuk menghapus catatan secara permanen
                showDeleteConfirmationDialog(trash)
            }
        )

        // Menghubungkan adapter ke RecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mengamati perubahan data di ViewModel untuk memperbarui tampilan RecyclerView
        trashViewModel.allTrash.observe(this) { trashList ->
            // Memperbarui data dalam adapter
            adapter.updateData(trashList)

            // Menampilkan pesan "kosong" jika tidak ada catatan di trash
            findViewById<TextView>(R.id.empty_view).visibility =
                if (trashList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    // Menampilkan dialog konfirmasi untuk penghapusan permanen catatan
    private fun showDeleteConfirmationDialog(trash: Trash) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Permanen")
            .setMessage("Apakah Anda yakin ingin menghapus catatan ini secara permanen?")
            .setPositiveButton("Ya") { _, _ ->
                // Menghapus catatan secara permanen melalui ViewModel
                trashViewModel.permanentlyDelete(trash)
                Toast.makeText(this, "Catatan dihapus permanen", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null) // Menutup dialog tanpa melakukan apa-apa
            .show()
    }
}
