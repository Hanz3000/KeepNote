package com.example.keepnote.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.keepnote.NoteApplication
import com.example.keepnote.R
import com.example.keepnote.databinding.ActivityAddNoteBinding
import com.example.keepnote.entity.Note
import com.example.keepnote.viewmodel.NoteViewModel
import com.example.keepnote.viewmodel.NoteViewModelFactory
import kotlin.getValue

@Suppress("DEPRECATION")
class AddNoteActivity : AppCompatActivity() {

    // Binding untuk layout activity_add_note.xml
    private lateinit var binding: ActivityAddNoteBinding

    // ViewModel untuk operasi yang terkait dengan catatan
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            // Mengambil DAO dari NoteApplication untuk akses database
            (application as NoteApplication).database.noteDao(),
            (application as NoteApplication).database.categoryDao(),
            (application as NoteApplication).database.trashDao()
        )
    }

    // Adapter untuk spinner kategori
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private val categories: MutableList<String> = mutableListOf()

    companion object {
        const val REQUEST_CODE_ADD_CATEGORY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur Data Binding dengan layout activity_add_note.xml
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        // Mengambil atau menerima data dari main activity
        val isEdit = intent.getBooleanExtra("IS_EDIT", false) //jika bernilai false maka akan menambahkan catatan baru
        val noteId = intent.getLongExtra("NOTE_ID", -1L)
        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: ""
        val noteContent = intent.getStringExtra("NOTE_CONTENT") ?: ""
        val noteCategory = intent.getStringExtra("NOTE_CATEGORY") ?: "Pilih Kategori" // Kategori default

        // Jika sedang mengedit catatan, isi EditText dengan data catatan yang diterima
        if (isEdit && noteId != -1L) {
            binding.editTextTitle.setText(noteTitle)
            binding.editTextContent.setText(noteContent)
        }

        // Mengatur spinner untuk menampilkan kategori, dengan kategori terpilih jika ada
        setupCategorySpinner(noteCategory)

        // Event handler untuk tombol Simpan fungsi untuk menyimpan inputan
        binding.btnSimpan.setOnClickListener {
            // Mengambil input dari pengguna
            val selectedCategory = binding.spinnerCategory.selectedItem.toString()
            val title = binding.editTextTitle.text.toString().trim()
            val content = binding.editTextContent.text.toString().trim()

            // Validasi input - semua field harus diisi
            if (title.isEmpty() || content.isEmpty() || selectedCategory == "Pilih Kategori") {
                Toast.makeText(this, "Judul, isi, dan kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                if (isEdit && noteId != -1L) { //jika benar maka akan mengupdate
                    // Mengupdate catatan yang sudah ada
                    noteViewModel.update(Note(noteId, title, content, selectedCategory))
                    Toast.makeText(this, "Catatan berhasil diubah", Toast.LENGTH_SHORT).show()
                } else {
                    // Menambahkan catatan baru
                    noteViewModel.insert(Note(title = title, content = content, category = selectedCategory))
                    Toast.makeText(this, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
                // Kembali ke MainActivity setelah catatan disimpan
                finish()
            }
        }

//        1. Event handler untuk tombol tambah kategori
        binding.buttonAddCategory.setOnClickListener {
            // Memulai CategoryActivity untuk menambah kategori baru
            val intent = Intent(this, CategoryActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_CATEGORY) //dimulai dengan startactivity
        }
    }

    // Menampilkan Kategori di Halaman Input (AddNoteActivity)
    private fun setupCategorySpinner(selectedCategory: String) {
        // Mengamati kategori yang disimpan di ViewModel
        noteViewModel.getAllCategoryNames().observe(this) { categoryNames ->
            // Membersihkan dan memperbarui daftar kategori
            categories.clear()

            // Menambahkan kategori yang diambil dari database
            categories.addAll(categoryNames)

            // Menambahkan item default "Pilih Kategori"
            categories.add(0, "Pilih Kategori")

            // Inisialisasi adapter untuk spinner kategori
            categoryAdapter = ArrayAdapter(this@AddNoteActivity, android.R.layout.simple_spinner_item, categories) //menghubungkan ke spiner
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) //menetapkan dropdown
            binding.spinnerCategory.adapter = categoryAdapter //menghubungkan adaptercategori ke spiner kategori

            // Menetapkan kategori yang dipilih jika sedang mengedit catatan
            selectCategory(selectedCategory)
        }
    }

    // Memilih kategori tertentu dalam spinner berdasarkan nama kategori
    private fun selectCategory(categoryName: String) {
        // Mencari posisi kategori dalam daftar
        val position = categories.indexOf(categoryName)
        if (position >= 0) {
            // Menetapkan posisi spinner sesuai kategori yang dipilih
            binding.spinnerCategory.setSelection(position)
        }
    }

    // Menerima hasil dari aktivitas dari category activity , MEMANGGIL ON ACTIVITY RESULT UNTUK MENERIMA seperti menambah kategori
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_CATEGORY && resultCode == Activity.RESULT_OK) {
            // Mengambil kategori baru yang ditambahkan dari CategoryActivity
            val newCategory = data?.getStringExtra("NEW_CATEGORY_NAME")
            newCategory?.let {
                // Menambahkan kategori baru ke daftar dan memperbarui adapter spinner
                categories.add(it)
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }
}
