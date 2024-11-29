package com.example.keepnote.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.databinding.HeaderItemBinding
import com.example.keepnote.databinding.NoteItemBinding
import com.example.keepnote.entity.Note

// Adapter untuk RecyclerView yang menampilkan daftar catatan
class NoteAdapter(
    private val onNoteClick: (Note) -> Unit
) : ListAdapter<Any, RecyclerView.ViewHolder>(NoteDiffCallback()) {

    private var data = arrayListOf<Any>()

    // ViewHolder adalah kelas yang merepresentasikan setiap item dalam RecyclerView
    inner class NoteViewHolder(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.note = note
            binding.executePendingBindings()

            // Listener untuk klik pada item catatan
            binding.root.setOnClickListener {
                onNoteClick(note)
            }
        }
    }

    inner class HeaderViewHolder(private val binding: HeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: String) {
            binding.textViewKategori.text = category
        }
    }

    // Fungsi untuk membuat ViewHolder sesuai tipe
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val noteBinding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val headerBinding = HeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return when (viewType) {
            ITEM_VIEW_TYPE.CONTENT.ordinal -> NoteViewHolder(noteBinding)
            else -> HeaderViewHolder(headerBinding)
        }
    }

    // Fungsi untuk mengikat data ke ViewHolder sesuai tipe
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteViewHolder -> {
                val currentNote = getItem(position) as? Note // Menggunakan as? untuk konversi aman
                currentNote?.let { holder.bind(it) }
            }
            is HeaderViewHolder -> {
                val category = getItem(position) as? String // Menggunakan as? untuk konversi aman
                category?.let { holder.bind(it) }
            }
        }
    }

    // Fungsi untuk memperbarui daftar catatan yang sudah dikelompokkan berdasarkan kategori
    fun submitNoteList(notes: ArrayList<Any>) {
        data.clear()
        data.addAll(notes) // Menggunakan addAll untuk menambahkan setiap item di dalam notes
        super.submitList(data.toList()) // Mengirimkan daftar yang terurut ke submitList
    }

    // NoteDiffCallback tetap digunakan untuk membandingkan catatan
    class NoteDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Note && newItem is Note -> oldItem.id == newItem.id
                else -> false
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }

    // Menentukan tipe tampilan item apakah kategori atau catatan
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Note -> ITEM_VIEW_TYPE.CONTENT.ordinal
            else -> ITEM_VIEW_TYPE.HEADER.ordinal
        }
    }

    enum class ITEM_VIEW_TYPE { HEADER, CONTENT }
}