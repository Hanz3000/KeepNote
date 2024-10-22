package com.example.keepnote.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.keepnote.R
import kotlin.getValue

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            (application as NoteApplication).database.noteDao(),
            (application as NoteApplication).database.categoryDao(),
            (application as NoteApplication).database.trashDao()
        )
    }

    private lateinit var categoryAdapter: ArrayAdapter<String>
    private val categories: MutableList<String> = mutableListOf()

    companion object {
        const val REQUEST_CODE_ADD_CATEGORY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        // Mengambil data dari Intent
        val isEdit = intent.getBooleanExtra("IS_EDIT", false)
        val noteId = intent.getLongExtra("NOTE_ID", -1L)
        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: ""
        val noteContent = intent.getStringExtra("NOTE_CONTENT") ?: ""
        val noteCategory = intent.getStringExtra("NOTE_CATEGORY") ?: "Pilih Kategori" // Mengambil kategori

        // Jika mengedit catatan, isi EditText dengan data catatan yang diterima
        if (isEdit && noteId != -1L) {
            binding.editTextTitle.setText(noteTitle)
            binding.editTextContent.setText(noteContent)
        }

        // Mengatur spinner kategori
        setupCategorySpinner(noteCategory) // Mengatur spinner dengan kategori yang sesuai

        // Menangani klik pada tombol Simpan
        binding.btnSimpan.setOnClickListener {
            val title = binding.editTextTitle.text.toString().trim()
            val content = binding.editTextContent.text.toString().trim()
            val selectedCategory = binding.spinnerCategory.selectedItem.toString()

            if (title.isEmpty() || content.isEmpty() || selectedCategory == "Pilih Kategori") {
                Toast.makeText(this, "Judul, isi, dan kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                if (isEdit && noteId != -1L) {
                    // Mengupdate catatan yang sudah ada
                    noteViewModel.update(Note(noteId, title, content, selectedCategory))
                    Toast.makeText(this, "Catatan berhasil diubah", Toast.LENGTH_SHORT).show()
                } else {
                    // Menambahkan catatan baru
                    noteViewModel.insert(Note(title = title, content = content, category = selectedCategory))
                    Toast.makeText(this, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
                finish() // Kembali ke MainActivity setelah menyimpan
            }
        }

        // Menangani klik pada tombol untuk menambah kategori
        binding.buttonAddCategory.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_CATEGORY)
        }
    }

    private fun setupCategorySpinner(selectedCategory: String) {
        // Mengamati kategori dari ViewModel
        noteViewModel.getAllCategoryNames().observe(this) { categoryNames ->
            categories.clear()

            // Tambahkan kategori dari database (jika ada)
            categories.addAll(categoryNames)

            // Tambahkan item default "Pilih Kategori" di posisi awal
            categories.add(0, "Pilih Kategori")

            // Inisialisasi adapter dengan kategori yang sudah diisi
            categoryAdapter = ArrayAdapter(this@AddNoteActivity, android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = categoryAdapter

            // Menetapkan kategori yang dipilih saat pengeditan
            selectCategory(selectedCategory)
        }
    }



    private fun selectCategory(categoryName: String) {
        // Menemukan posisi kategori dalam daftar dan menetapkan spinner ke kategori yang sesuai
        val position = categories.indexOf(categoryName)
        if (position >= 0) {
            binding.spinnerCategory.setSelection(position) // Menetapkan kategori yang dipilih
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_CATEGORY && resultCode == Activity.RESULT_OK) {
            val newCategory = data?.getStringExtra("NEW_CATEGORY_NAME")
            newCategory?.let {
                categories.add(it) // Menambahkan kategori baru ke daftar
                categoryAdapter.notifyDataSetChanged() // Memperbarui tampilan spinner
            }
        }
    }
}
