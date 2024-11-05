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

    private lateinit var binding: ActivityMainBinding // Binding untuk layout tanpa menggunakan find by id
    private lateinit var categorySpinner: Spinner // Spinner untuk memilih kategori
    private lateinit var categoryAdapter: ArrayAdapter<String> // Adapter untuk daftar kategori
    private val noteViewModel: NoteViewModel by viewModels { // Inisialisasi ViewModel untuk mengelola dan menyediakan data catatan
        NoteViewModelFactory(
            (application as NoteApplication).database.noteDao(),
            (application as NoteApplication).database.categoryDao(),
            (application as NoteApplication).database.trashDao()
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur binding untuk menghubungkan layout activity_main.xml dengan kode di MainActivity
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Menyambungkan ViewModel dengan binding, sehingga ViewModel dapat diakses langsung dari layout XML
        binding.viewModel = noteViewModel
        // Menentukan lifecycle owner untuk binding, sehingga UI dapat otomatis ter-update sesuai lifecycle
        binding.lifecycleOwner = this

        // Inisialisasi adapter dan layout manager untuk RecyclerView
        val adapter = NoteAdapter { note -> onNoteClick(note) } // Ketika item diklik, akan memanggil fungsi onNoteClick dengan data note
        binding.recyclerView.layoutManager = LinearLayoutManager(this)  // LinearLayoutManager akan menampilkan item dalam daftar vertikal (seperti daftar sederhana)
        binding.recyclerView.adapter = adapter // Adapter bertugas menyediakan data catatan ke RecyclerView  mengontrol bagaimana item tampil

        setupItemTouchHelper(adapter) // Mengatur swipe untuk menghapus catatan
        setupCategorySpinner(adapter) // Mengatur spinner kategori dan memfilter catatan

        // Mengamati perubahan pada daftar catatan dan memperbarui adapter
        noteViewModel.allNotes.observe(this) { notes ->
            adapter.setNotes(notes)
            updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong
        }

        //  Ketika pengguna menekan tombol FAB, AddNoteActivity terbuka.
        binding.fabaddnote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }


        // Mengatur onClickListener untuk FAB tempat sampah
        binding.fabTrash.setOnClickListener {
            val intent = Intent(this, TrashActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupItemTouchHelper(adapter: NoteAdapter) { //mengatur mekanisme swipe pada item RecyclerView untuk menghapus catatan.
        // Mengatur swipe gesture untuk menghapus catatan
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = adapter.getNoteAt(viewHolder.adapterPosition)
                showDeleteNoteDialog(note, adapter, viewHolder.adapterPosition) //onSwiped akan dipanggil untuk mengambil catatan yang dipilih dari adapter dan menampilkan dialog konfirmasi hapus.
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }

    // 1.filter
    private fun setupCategorySpinner(adapter: NoteAdapter) {
        categorySpinner = binding.categorySpinner // Ambil spinner dari layout

        // Mengamati daftar kategori dan memperbarui adapter Spinner
        noteViewModel.getAllCategories().observe(this) { categories ->
            val allCategories = mutableListOf("Semua").apply { addAll(categories) } // Menambahkan opsi "Semua" ke daftar kategori sehingga pengguna dapat melihat semua note
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allCategories).apply { //Membuat ArrayAdapter untuk menampilkan daftar kategori di Spinner.
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Mengatur layout untuk dropdown
            }
            categorySpinner.adapter = categoryAdapter // Mengatur adapter untuk Spinner
            // Mengatur listener untuk pilihan kategori
            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener { //untuk menangani perubahan pada kategori yang dipilih.

                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCategory = categorySpinner.selectedItem.toString() // Mendapatkan kategori yang dipilih
                    filterNotesByCategory(selectedCategory, adapter) // saat category dipilih maka akan Memfilter catatan berdasarkan kategori
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Jika tidak ada kategori yang dipilih, tampilkan semua catatan
                    noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                        adapter.setNotes(notes)
                        updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong
                    }
                }
            }

            //1. Menangani long click pada kategori untuk menghapus kategori
            categorySpinner.setOnLongClickListener {
                val selectedCategory = categorySpinner.selectedItem.toString()
                if (selectedCategory != "Semua") {
                    showDeleteCategoryDialog(selectedCategory) // Menampilkan dialog konfirmasi penghapusan kategori
                }
                true
            }
        }
    }
    //2. Filter Catatan Berdasarkan Kategori (filterNotesByCategory)
    private fun filterNotesByCategory(category: String, adapter: NoteAdapter) {
        // Memfilter catatan berdasarkan kategori yang dipilih
        when (category) {
            "Semua" -> noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                adapter.setNotes(notes)
                updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong
            }
            else -> {
                // Jika kategori tertentu dipilih, tampilkan catatan sesuai kategori
                noteViewModel.getNotesByCategory(category).observe(this@MainActivity) { notes ->
                    adapter.setNotes(notes)
                    updateEmptyView(notes.isEmpty()) // Memperbarui tampilan kosong jika tidak ada catatan maka akan muncul pesan kosong
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
        //2. Menampilkan dialog konfirmasi untuk menghapus kategori
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori '$categoryName'?")
            .setPositiveButton("Ya") { _, _ -> deleteCategory(categoryName) } // Menghapus kategori jika dikonfirmasi
            .setNegativeButton("Tidak", null) // Menutup dialog jika tidak
            .show()
    }

    //3.hapus kategori dari aler iya
    private fun deleteCategory(categoryName: String) { //deleate kategori
        noteViewModel.deleteCategory(categoryName) // Menghapus kategori
        Toast.makeText(this, "Kategori '$categoryName' telah dihapus", Toast.LENGTH_SHORT).show() // Menampilkan pesan konfirmasi
        categorySpinner.setSelection(0) // Reset pilihan Spinner ke "Semua" setelah menghapus kategori
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        // 3. Memperbarui tampilan berdasarkan apakah ada catatan
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE // Menampilkan atau menyembunyikan tampilan kosong
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE // Menampilkan note atau menyembunyikan note dari RecyclerView
    }

    // Menangani klik pada catatan untuk mengeditnya
    private fun onNoteClick(note: Note) {
        // Memindahkan ke AddNoteActivity dengan data catatan yang ingin diedit
        val intent = Intent(this, AddNoteActivity::class.java).apply {
            putExtra("NOTE_ID", note.id) // Mengirim ID catatan
            putExtra("NOTE_TITLE", note.title) // Mengirim judul catatan
            putExtra("NOTE_CONTENT", note.content) // Mengirim konten catatan
            putExtra("NOTE_CATEGORY", note.category) // Mengirim kategori catatan
            putExtra("IS_EDIT", true) // mengirim nilai bollean true yang Menandakan bahwa ini adalah mode edit
        }
        startActivity(intent) // Memulai aktivitas untuk mengedit catatan
    }
}
