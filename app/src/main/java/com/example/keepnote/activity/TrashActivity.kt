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

    private lateinit var trashViewModel: TrashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        val trashDao = (application as NoteApplication).database.trashDao()
        val noteDao = (application as NoteApplication).database.noteDao()
        val factory = TrashViewModelFactory(trashDao, noteDao)
        trashViewModel = viewModels<TrashViewModel> { factory }.value

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_trash)
        val adapter = TrashAdapter(
            emptyList(),
            { trash ->
                trashViewModel.recover(trash)
                Toast.makeText(this, "Catatan dipulihkan", Toast.LENGTH_SHORT).show()
            },
            { trash ->
                showDeleteConfirmationDialog(trash)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        trashViewModel.allTrash.observe(this) { trashList ->
            adapter.updateData(trashList)
            findViewById<TextView>(R.id.empty_view).visibility =
                if (trashList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showDeleteConfirmationDialog(trash: Trash) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Permanen")
            .setMessage("Apakah Anda yakin ingin menghapus catatan ini secara permanen?")
            .setPositiveButton("Ya") { _, _ ->
                trashViewModel.permanentlyDelete(trash)
                Toast.makeText(this, "Catatan dihapus permanen", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}