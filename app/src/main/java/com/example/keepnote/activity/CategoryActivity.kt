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

    // Binding untuk layout activity_add_category.xml
    private lateinit var binding: ActivityAddCategoryBinding

    // ViewModel untuk operasi terkait kategori
    private val categoryViewModel: CategoryViewModel by viewModels {
        // Menggunakan CategoryViewModelFactory untuk menginisialisasi ViewModel dengan DAO kategori
        CategoryViewModelFactory((application as NoteApplication).database.categoryDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur Data Binding dengan layout activity_add_category.xml
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_category)

        // Event handler untuk tombol Simpan, digunakan untuk menambah kategori baru
        binding.btnSimpan.setOnClickListener {
            // Mengambil input nama kategori dari pengguna
            val categoryName = binding.editTextCategoryName.text.toString().trim()

            // Memastikan nama kategori tidak kosong
            if (categoryName.isNotEmpty()) {
                // Menambahkan kategori baru ke database melalui ViewModel MENGIRIM KE VIEW MODEL
                categoryViewModel.insert(Category(name = categoryName))

                // Mengirim hasil ke AddNoteActivity menggunakan Intent menggunakann putexstara
                val resultIntent = Intent()
                resultIntent.putExtra("NEW_CATEGORY_NAME", categoryName)
                setResult(Activity.RESULT_OK, resultIntent)

                // Menutup CategoryActivity setelah menyimpan kategori
                finish()
            } else {
                // Menampilkan pesan kesalahan jika nama kategori kosong
                Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Event handler untuk tombol Batal, digunakan untuk membatalkan penambahan kategori
        binding.btnBatal.setOnClickListener {
            // Menutup CategoryActivity tanpa menyimpan data
            finish()
        }
    }
}
