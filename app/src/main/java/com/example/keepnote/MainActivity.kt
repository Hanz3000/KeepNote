package com.example.keepnote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.activity.AboutActivity
import com.example.keepnote.activity.AddNoteActivity
import com.example.keepnote.activity.SettingsActivity
import com.example.keepnote.activity.TrashActivity
import com.example.keepnote.adapter.NoteAdapter
import com.example.keepnote.databinding.ActivityMainBinding
import com.example.keepnote.entity.Note
import com.example.keepnote.viewmodel.NoteViewModel
import com.example.keepnote.viewmodel.NoteViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var categorySpinner: Spinner
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var adapter: NoteAdapter

    private val data = arrayListOf<Any>()

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            (application as NoteApplication).database.noteDao(),
            (application as NoteApplication).database.categoryDao(),
            (application as NoteApplication).database.trashDao()
        )
    }


    private val categoriesRef = FirebaseDatabase.getInstance().getReference("categories")
    private val deletedNotesRef = FirebaseDatabase.getInstance().getReference("deleted_notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengambil tema yang sudah dipilih sebelumnya
        val theme = getAppTheme(this)
        setAppTheme(this, theme)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.viewModel = noteViewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        setupCategorySpinner()
        observeNotes()

        // SearchView listener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterNotesByQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterNotesByQuery(it) }
                return true
            }
        })

        // FAB click listener
        binding.fabaddnote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_trash -> {
                startActivity(Intent(this, TrashActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter { note -> onNoteClick(note) }
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (data[position]) {
                    is Note -> 1
                    else -> 2
                }
            }
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        setupItemTouchHelper(adapter)
    }

    private fun setupItemTouchHelper(adapter: NoteAdapter) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val position = viewHolder.adapterPosition
                return if (position != RecyclerView.NO_POSITION && data[position] is String) {
                    0 // selain note tidak akan bisa bergeser
                } else {
                    super.getSwipeDirs(recyclerView, viewHolder)
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = data[position]
                if (item is Note) {
                    showDeleteNoteDialog(item, adapter, position)
                } else {
                    adapter.notifyItemChanged(position)
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerView)
    }

    private fun setupCategorySpinner() {
        categorySpinner = binding.categorySpinner
        noteViewModel.getAllCategoryNames().observe(this) { categories ->
            val allCategories = mutableListOf("Semua").apply { addAll(categories) }
            categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allCategories).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            categorySpinner.adapter = categoryAdapter

            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedCategory = categorySpinner.selectedItem.toString()
                    filterNotesByCategory(selectedCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    noteViewModel.allNotes.observe(this@MainActivity) { notes ->
                        submitNoteList(notes)
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
    private fun observeNotes() {
        noteViewModel.allNotes.observe(this) { notes ->
            submitNoteList(notes)
            updateEmptyView(notes.isEmpty())
        }
    }
    private fun filterNotesByCategory(category: String) {
        if (category == "Semua") {
            noteViewModel.allNotes.observe(this) { notes ->
                submitNoteList(notes)
                updateEmptyView(notes.isEmpty())
            }
        } else {
            noteViewModel.getNotesByCategory(category).observe(this) { notes ->
                submitNoteList(notes)
                updateEmptyView(notes.isEmpty())
            }
        }
    }

    private fun filterNotesByQuery(query: String) {
        noteViewModel.searchNotes(query).observe(this) { notes ->
            submitNoteList(notes)
            updateEmptyView(notes.isEmpty())
        }
    }

    private fun showDeleteNoteDialog(note: Note, adapter: NoteAdapter, position: Int) {
        AlertDialog.Builder(this)
            .setMessage("Anda yakin ingin menghapus catatan '${note.title}'?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, _ ->
                saveDeletedNoteToFirebase(note)
                noteViewModel.delete(note)
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .show()
    }

    private fun saveDeletedNoteToFirebase(note: Note) {
        val noteId = note.id.toString()
        deletedNotesRef.child(noteId).setValue(note)
            .addOnSuccessListener {
                Toast.makeText(this, "Catatan '${note.title}' berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal dihapus: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteCategoryDialog(categoryName: String) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus kategori '$categoryName'?")
            .setPositiveButton("Ya") { _, _ -> deleteCategory(categoryName) }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun deleteCategory(categoryName: String) {
        // Mencari kategori berdasarkan name
        categoriesRef.orderByChild("name").equalTo(categoryName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (categorySnapshot in snapshot.children) {
                            // Hapus node kategori berdasarkan id
                            categorySnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    Log.d("DeleteCategory", "Kategori berhasil dihapus dari Firebase")
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Kategori '$categoryName' telah dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    categorySpinner.setSelection(0)
                                    noteViewModel.deleteCategoryByName(categoryName)
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("DeleteCategory", "Gagal menghapus kategori", exception)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Gagal menghapus kategori: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Log.d("DeleteCategory", "Kategori '$categoryName' tidak ditemukan di Firebase")
                        Toast.makeText(
                            this@MainActivity,
                            "Kategori tidak ditemukan di Firebase",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DeleteCategory", "Error: ${error.message}", error.toException())
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun onNoteClick(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java).apply {
            putExtra("NOTE_ID", note.id)  // Pastikan mengirimkan note.id sebagai String
            putExtra("NOTE_TITLE", note.title)
            putExtra("NOTE_CONTENT", note.content)
            putExtra("NOTE_CATEGORY", note.category)
            putExtra("IS_EDIT", true)  // Menandakan bahwa ini adalah mode edit
        }
        startActivity(intent)
    }
    private fun submitNoteList(notes: List<Note>) {
        val groupedNotes = notes.groupBy { it.category }
        data.clear()

        groupedNotes.forEach { (category, notesInCategory) ->
            data.add(category)
            data.addAll(notesInCategory)
        }
        adapter.submitNoteList(data as ArrayList<Any>)
    }

    // Fungsi untuk mengganti tema
    private fun     setAppTheme(context: Context, theme: String) {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPref.edit().putString("APP_THEME", theme).apply()

        when (theme) {
            "LIGHT" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "DARK" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    // Fungsi untuk mengambil tema yang dipilih
    private fun getAppTheme(context: Context): String {
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPref.getString("APP_THEME", "LIGHT") ?: "LIGHT" // Default ke terang
    }
}