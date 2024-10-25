package com.example.keepnote

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.activity.AddNoteActivity
import com.example.keepnote.activity.TrashActivity
import com.example.keepnote.adapter.NoteAdapter
import com.example.keepnote.databinding.ActivityMainBinding
import com.example.keepnote.entity.Note
import com.example.keepnote.viewmodel.NoteViewModel
import com.example.keepnote.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Binding untuk layout
    private lateinit var categorySpinner: Spinner // Spinner untuk memilih kategori
    private lateinit var categoryAdapter: ArrayAdapter<String> // Adapter untuk kategori
    private val noteViewModel: NoteViewModel by viewModels { // Inisialisasi ViewModel
        NoteViewModelFactory(
            (application as NoteApplication).database.noteDao(),
            (application as NoteApplication).database.categoryDao(),
            (application as NoteApplication).database.trashDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur binding dan ViewModel
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = noteViewModel
        binding.lifecycleOwner = this // Menentukan lifecycle owner untuk data binding

        // Inisialisasi adapter dan layout manager untuk RecyclerView
        val adapter = NoteAdapter { note -> onNoteClick(note) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        setupItemTouchHelper(adapter) // Mengatur swipe untuk menghapus catatan
        setupCategorySpinner(adapter) // Mengatur spinner kategori

        // Mengamati perubahan pada daftar catatan dan memperbarui adapter
        noteViewModel.allNotes.observe(this) { notes ->
            adapter.setNotes(notes)
            updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong
        }

        // Mengatur onClickListener untuk FAB menambah catatan
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        // Mengatur onClickListener untuk FAB tempat sampah
        binding.fabTrash.setOnClickListener {
            val intent = Intent(this, TrashActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupItemTouchHelper(adapter: NoteAdapter) {
        // Mengatur swipe gesture untuk menghapus catatan
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = adapter.getNoteAt(viewHolder.adapterPosition)
                showDeleteNoteDialog(note, adapter, viewHolder.adapterPosition) // Menampilkan dialog konfirmasi penghapusan
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun setupCategorySpinner(adapter: NoteAdapter) {
        categorySpinner = binding.categorySpinner // Mengambil referensi Spinner

        // Mengamati daftar kategori dan memperbarui adapter Spinner
        noteViewModel.getAllCategories().observe(this) { categories ->
            val allCategories = mutableListOf("Semua").apply { addAll(categories) } // Menambahkan opsi "Semua" ke daftar kategori
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allCategories).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Mengatur layout untuk dropdown
            }
            categorySpinner.adapter = categoryAdapter // Mengatur adapter untuk Spinner

            // Mengatur listener untuk pilihan kategori
            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCategory = categorySpinner.selectedItem.toString() // Mendapatkan kategori yang dipilih
                    filterNotesByCategory(selectedCategory, adapter) // Memfilter catatan berdasarkan kategori
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Jika tidak ada kategori yang dipilih, tampilkan semua catatan
                    noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                        adapter.setNotes(notes)
                        updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong
                    }
                }
            }

            // Menangani long click pada kategori untuk menghapus kategori
            categorySpinner.setOnLongClickListener {
                val selectedCategory = categorySpinner.selectedItem.toString()
                if (selectedCategory != "Semua") {
                    showDeleteCategoryDialog(selectedCategory) // Menampilkan dialog konfirmasi penghapusan kategori
                }
                true
            }
        }
    }

    private fun filterNotesByCategory(category: String, adapter: NoteAdapter) {
        // Memfilter catatan berdasarkan kategori yang dipilih
        when (category) {
            "Semua" -> noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                adapter.setNotes(notes)
                updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong
            }
            else -> {
                noteViewModel.getNotesByCategory(category).observe(this@MainActivity) { notes ->
                    adapter.setNotes(notes)
                    updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong
                }
            }
        }
    }

    private fun showDeleteNoteDialog(note: Note, adapter: NoteAdapter, position: Int) {
        // Menampilkan dialog konfirmasi untuk menghapus catatan
        AlertDialog.Builder(this)
            .setMessage("Anda yakin ingin menghapus catatan '${note.title}'?") // Pesan dialog
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, _ ->
                noteViewModel.delete(note) // Menghapus catatan
                dialog.dismiss() // Menutup dialog
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                adapter.notifyItemChanged(position) // Mengembalikan item ke posisi semula
                dialog.dismiss() // Menutup dialog
            }
            .show()
    }

    private fun showDeleteCategoryDialog(categoryName: String) {
        // Menampilkan dialog konfirmasi untuk menghapus kategori
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori '$categoryName'?")
            .setPositiveButton("Ya") { _, _ -> deleteCategory(categoryName) } // Menghapus kategori jika dikonfirmasi
            .setNegativeButton("Tidak", null) // Menutup dialog jika tidak
            .show()
    }

    private fun deleteCategory(categoryName: String) {
        noteViewModel.deleteCategory(categoryName) // Menghapus kategori
        Toast.makeText(this, "Kategori '$categoryName' telah dihapus", Toast.LENGTH_SHORT).show() // Menampilkan pesan konfirmasi
        categorySpinner.setSelection(0) // Reset pilihan Spinner ke "Semua" setelah menghapus kategori
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        // Memperbarui tampilan berdasarkan apakah ada catatan
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE // Menampilkan atau menyembunyikan tampilan kosong
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE // Menampilkan atau menyembunyikan RecyclerView
    }

    private fun onNoteClick(note: Note) {
        // Menangani klik pada catatan untuk mengeditnya
        val intent = Intent(this, AddNoteActivity::class.java).apply {
            putExtra("NOTE_ID", note.id) // Mengirim ID catatan
            putExtra("NOTE_TITLE", note.title) // Mengirim judul catatan
            putExtra("NOTE_CONTENT", note.content) // Mengirim konten catatan
            putExtra("NOTE_CATEGORY", note.category) // Mengirim kategori catatan
            putExtra("IS_EDIT", true) // Menandakan bahwa ini adalah mode edit
        }
        startActivity(intent) // Memulai aktivitas untuk mengedit catatan
    }
}
