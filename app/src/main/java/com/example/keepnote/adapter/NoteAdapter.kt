package com.example.keepnote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.databinding.NoteItemBinding
import com.example.keepnote.entity.Note

class NoteAdapter(private val onNoteClick: (Note) -> Unit) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private var notes = emptyList<Note>() // Daftar catatan yang akan ditampilkan

    inner class NoteViewHolder(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.note = note
            binding.executePendingBindings() // Memastikan binding dieksekusi segera

            // Mengatur listener klik pada item
            binding.root.setOnClickListener {
                onNoteClick(note) // Memanggil listener ketika item diklik
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.bind(currentNote)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    // Memperbarui daftar catatan dan memberi tahu RecyclerView tentang perubahan
    fun setNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged() // Memberitahukan RecyclerView bahwa data telah diubah
    }

    // Mengembalikan item pada posisi tertentu
    fun getNoteAt(position: Int): Note {
        return notes[position]
    }
}
