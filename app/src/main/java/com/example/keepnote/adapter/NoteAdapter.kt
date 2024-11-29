package com.example.keepnote.adapter

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

    // Dipanggil untuk mengikat data ke ViewHolder yang sesuai dengan posisi
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = getItem(position) // Mengambil catatan pada posisi yang diberikan
        holder.bind(currentNote) // Mengikat catatan ke ViewHolder
    }



    class NoteDiffCallback : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(data[position]){
            is Note -> ITEM_VIEW_TYPE.CONTENT.ordinal
            else -> ITEM_VIEW_TYPE.HEADER.ordinal
        }
    }

    enum class ITEM_VIEW_TYPE {HEADER, CONTENT }

}
