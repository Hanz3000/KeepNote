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

    private lateinit var binding: ActivityMainBinding
    private lateinit var categorySpinner: Spinner
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            (application as NoteApplication).database.noteDao(),
            (application as NoteApplication).database.categoryDao(),
            (application as NoteApplication).database.trashDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = noteViewModel
        binding.lifecycleOwner = this

        val adapter = NoteAdapter { note -> onNoteClick(note) }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        setupItemTouchHelper(adapter)
        setupCategorySpinner(adapter)

        noteViewModel.allNotes.observe(this) { notes ->
            adapter.setNotes(notes)
            updateEmptyView(notes.isEmpty())
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        binding.fabTrash.setOnClickListener {
            val intent = Intent(this, TrashActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupItemTouchHelper(adapter: NoteAdapter) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = adapter.getNoteAt(viewHolder.adapterPosition)
                showDeleteNoteDialog(note, adapter, viewHolder.adapterPosition)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun setupCategorySpinner(adapter: NoteAdapter) {
        categorySpinner = binding.categorySpinner

        noteViewModel.getAllCategories().observe(this) { categories ->
            val allCategories = mutableListOf("Semua").apply { addAll(categories) }
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allCategories).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            categorySpinner.adapter = categoryAdapter

            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCategory = categorySpinner.selectedItem.toString()
                    filterNotesByCategory(selectedCategory, adapter)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                        adapter.setNotes(notes)
                        updateEmptyView(notes.isEmpty())
                    }
                }
            }

            categorySpinner.setOnLongClickListener {
                val selectedCategory = categorySpinner.selectedItem.toString()
                if (selectedCategory != "Semua") {
                    showDeleteCategoryDialog(selectedCategory)
                }
                true
            }
        }
    }

    private fun filterNotesByCategory(category: String, adapter: NoteAdapter) {
        when (category) {
            "Semua" -> noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                adapter.setNotes(notes)
                updateEmptyView(notes.isEmpty())
            }
            else -> {
                noteViewModel.getNotesByCategory(category).observe(this@MainActivity) { notes ->
                    adapter.setNotes(notes)
                    updateEmptyView(notes.isEmpty())
                }
            }
        }
    }

    private fun showDeleteNoteDialog(note: Note, adapter: NoteAdapter, position: Int) {
        AlertDialog.Builder(this)
            .setMessage("Anda yakin ingin menghapus catatan '${note.title}'?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, _ ->
                noteViewModel.delete(note)
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteCategoryDialog(categoryName: String) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori '$categoryName'?")
            .setPositiveButton("Ya") { _, _ ->
                deleteCategory(categoryName)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun deleteCategory(categoryName: String) {
        noteViewModel.deleteCategory(categoryName)
        Toast.makeText(this, "Kategori '$categoryName' telah dihapus", Toast.LENGTH_SHORT).show()
        // Reset spinner selection to "Semua" after deleting a category
        categorySpinner.setSelection(0)
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun onNoteClick(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java).apply {
            putExtra("NOTE_ID", note.id)
            putExtra("NOTE_TITLE", note.title)
            putExtra("NOTE_CONTENT", note.content)
            putExtra("NOTE_CATEGORY", note.category)
            putExtra("IS_EDIT", true)
        }
        startActivity(intent)
    }
}