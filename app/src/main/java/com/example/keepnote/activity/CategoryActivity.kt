package com.example.keepnote.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.keepnote.NoteApplication
import com.example.keepnote.R
import com.example.keepnote.databinding.ActivityAddCategoryBinding
import com.example.keepnote.entity.Category
import com.example.keepnote.viewmodel.CategoryViewModel
import com.example.keepnote.viewmodel.CategoryViewModelFactory
import kotlin.getValue

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCategoryBinding // Perbarui baris ini
    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModelFactory((application as NoteApplication).database.categoryDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_category) // Perbarui baris ini

        // Menangani klik pada tombol Simpan untuk menambah kategori
        binding.btnSimpan.setOnClickListener {
            val categoryName = binding.editTextCategoryName.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                // Menyimpan kategori baru ke database
                categoryViewModel.insert(Category(name = categoryName))

                // Mengembalikan hasil ke AddNoteActivity
                val resultIntent = Intent()
                resultIntent.putExtra("NEW_CATEGORY_NAME", categoryName)
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Tutup CategoryActivity
            } else {
                Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Menangani klik pada tombol Batal
        binding.btnBatal.setOnClickListener {
            finish() // Tutup CategoryActivity tanpa menyimpan
        }
    }
}
