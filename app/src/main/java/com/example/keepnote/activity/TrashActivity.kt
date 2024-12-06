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
import com.google.firebase.database.FirebaseDatabase

class TrashActivity : AppCompatActivity() {

    private lateinit var trashViewModel: TrashViewModel
    private val trashRef = FirebaseDatabase.getInstance().getReference("trash")
    private val noteRef = FirebaseDatabase.getInstance().getReference("notes") // Referensi untuk menyimpan catatan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        val trashDao = (application as NoteApplication).database.trashDao()
        val noteDao = (application as NoteApplication).database.noteDao()
        val factory = TrashViewModelFactory(trashDao, noteDao)
        trashViewModel = viewModels<TrashViewModel> { factory }.value

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_trash)
        val adapter = TrashAdapter(
            { trash ->
                trashViewModel.recover(trash) // Pulihkan catatan
                trashRef.child(trash.id.toString()).removeValue() // Hapus dari trash di Firebase
                noteRef.child(trash.id.toString()).setValue(trash) // Simpan kembali ke Firebase notes
                Toast.makeText(this, "Catatan dipulihkan", Toast.LENGTH_SHORT).show()
            },
            { trash ->
                showDeleteConfirmationDialog(trash)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe data changes in Room and Firebase
        trashViewModel.allTrash.observe(this) { trashList ->
            adapter.submitList(trashList)
            findViewById<TextView>(R.id.empty_view).visibility =
                if (trashList.isEmpty()) View.VISIBLE else View.GONE
        }

        // Real-time listener for Firebase
        trashRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val firebaseTrash = snapshot.children.mapNotNull { it.getValue(Trash::class.java) }
                adapter.submitList(firebaseTrash)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(this@TrashActivity, "Gagal memuat data dari Firebase", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog(trash: Trash) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Permanen")
            .setMessage("Apakah Anda yakin ingin menghapus catatan ini secara permanen?")
            .setPositiveButton("Ya") { _, _ ->
                trashViewModel.permanentlyDelete(trash) // Hapus dari Room
                trashRef.child(trash.id.toString()).removeValue() // Hapus dari Firebase trash
                Toast.makeText(this, "Catatan dihapus permanen", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}