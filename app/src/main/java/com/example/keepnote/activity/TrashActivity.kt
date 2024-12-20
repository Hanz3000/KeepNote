package com.example.keepnote.activity

import android.os.Bundle
import android.view.MenuItem
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
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.FirebaseDatabase

class TrashActivity : AppCompatActivity() {

    private lateinit var trashViewModel: TrashViewModel // Menyimpan trashViewModel untuk mengelola data sampah dalam ViewModel
    private val trashRef = FirebaseDatabase.getInstance().getReference("trash")
    private val noteRef = FirebaseDatabase.getInstance().getReference("notes") // Referensi untuk menyimpan catatan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        // Menghubungkan variabel `toolbar` dengan MaterialToolbar dari layout
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Menambahkan tombol "kembali" di toolbar untuk navigasi
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Instance untuk akses data pada tabel
        val trashDao = (application as NoteApplication).database.trashDao()
        val noteDao = (application as NoteApplication).database.noteDao()
        val factory = TrashViewModelFactory(trashDao, noteDao)
        trashViewModel = viewModels<TrashViewModel> { factory }.value

        // Menghubungkan RecyclerView di layout dengan variabel untuk menampilkan daftar sampah
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_trash)
        val adapter = TrashAdapter(
            { trash ->
                trashViewModel.recover(trash) // Pulihkan catatan
                trashRef.child(trash.id.toString()).removeValue() // Hapus dari trash di Firebase
                noteRef.child(trash.noteId.toString()).setValue(trash) // Simpan kembali ke Firebase notes
                Toast.makeText(this, "Catatan dipulihkan", Toast.LENGTH_SHORT).show()
            },
            { trash ->
                showDeleteConfirmationDialog(trash)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mengamati perubahan pada daftar sampah (allTrash)
        trashViewModel.allTrash.observe(this) { trashList ->
            adapter.submitList(trashList)
            findViewById<TextView>(R.id.empty_view).visibility =
                if (trashList.isEmpty()) View.VISIBLE else View.GONE
        }

        // Menambahkan listener untuk perubahan data pada referensi trash di firebase
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

    // Menangani aksi klik pada tombol back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Kembali ke aktivitas sebelumnya
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
