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
import com.google.firebase.database.FirebaseDatabase

class AddNoteActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val notesRef = database.getReference("notes")

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        val isEdit = intent.getBooleanExtra("IS_EDIT", false)
        val noteId = intent.getStringExtra("NOTE_ID") ?: ""
        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: ""
        val noteContent = intent.getStringExtra("NOTE_CONTENT") ?: ""
        val noteCategory = intent.getStringExtra("NOTE_CATEGORY") ?: "Pilih Kategori"

        if (isEdit && noteId != null) {
            binding.editTextTitle.setText(noteTitle)
            binding.editTextContent.setText(noteContent)
        }

        setupCategorySpinner(noteCategory)

        binding.btnSimpan.setOnClickListener {
            val selectedCategory = binding.spinnerCategory.selectedItem.toString()
            val title = binding.editTextTitle.text.toString().trim()
            val content = binding.editTextContent.text.toString().trim()

            if (title.isEmpty() || content.isEmpty() || selectedCategory == "Pilih Kategori") {
                Toast.makeText(this, "Judul, isi, dan kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                if (isEdit && noteId.isNotEmpty()) {
                    val updatedNote = Note(noteId, title, content, selectedCategory)
                    noteViewModel.update(updatedNote)
                    Toast.makeText(this, "Catatan berhasil diubah", Toast.LENGTH_SHORT).show()
                } else {
                    val lastNoteId = getLastNoteId()
                    val newId = (lastNoteId + 1).toString()

                    val newNote = Note(id = newId, title = title, content = content, category = selectedCategory)
                    notesRef.child(newId).setValue(newNote)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Catatan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal menambahkan catatan: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    noteViewModel.insert(newNote)
                    updateLastNoteId(newId.toInt())
                }
                finish()
            }
        }

        binding.buttonAddCategory.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_CATEGORY)
        }
    }

    private fun getLastNoteId(): Int {
        val sharedPreferences = getSharedPreferences("NotePreferences", MODE_PRIVATE)
        return sharedPreferences.getInt("lastNoteId", 0)
    }

    private fun updateLastNoteId(lastId: Int) {
        val sharedPreferences = getSharedPreferences("NotePreferences", MODE_PRIVATE)
        sharedPreferences.edit().putInt("lastNoteId", lastId).apply()
    }

    private fun setupCategorySpinner(selectedCategory: String) {
        noteViewModel.getAllCategoryNames().observe(this) { categoryNames ->
            categories.clear()
            categories.addAll(categoryNames)
            categories.add(0, "Pilih Kategori")

            categoryAdapter = ArrayAdapter(this@AddNoteActivity, android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = categoryAdapter

            selectCategory(selectedCategory)
        }
    }

    private fun selectCategory(categoryName: String) {
        val position = categories.indexOf(categoryName)
        if (position >= 0) {
            binding.spinnerCategory.setSelection(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_CATEGORY && resultCode == Activity.RESULT_OK) {
            val newCategory = data?.getStringExtra("NEW_CATEGORY_NAME")
            newCategory?.let {
                categories.add(it)
                categoryAdapter.notifyDataSetChanged()
            }
        }
    }
}
