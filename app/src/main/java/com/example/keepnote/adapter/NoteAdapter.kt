package com.example.keepnote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.databinding.NoteItemBinding
import com.example.keepnote.entity.Note

// Adapter untuk RecyclerView yang menampilkan daftar catatan
class NoteAdapter(
    private val onNoteClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    // ViewHolder adalah kelas yang merepresentasikan setiap item dalam RecyclerView
    inner class NoteViewHolder(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Fungsi untuk mengikat data catatan ke tampilan (View) pada setiap item
        fun bind(note: Note) {
            binding.note = note // Mengikat objek Note ke layout melalui data binding
            binding.executePendingBindings() // Memastikan binding segera dieksekusi

            // Mengatur listener untuk menangani klik pada item catatan
            binding.root.setOnClickListener {
                onNoteClick(note) // Memanggil fungsi onNoteClick ketika item diklik
            }
        }
    }

    // Dipanggil saat ViewHolder baru dibuat
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        // Menginflate layout item catatan menggunakan data binding
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    // Dipanggil untuk mengikat data ke ViewHolder yang sesuai dengan posisi
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = getItem(position) // Mengambil catatan pada posisi yang diberikan
        holder.bind(currentNote) // Mengikat catatan ke ViewHolder
    }

    // Mengembalikan jumlah item catatan yang ada di dalam daftar
    //override fun getItemCount(): Int {
    //    return notes.size
    //}

    // Memperbarui daftar catatan dengan data yang baru
    //fun setNotes(newNotes: List<Note>) {
    //    notes = newNotes // Mengganti daftar catatan lama dengan yang baru
    //    notifyDataSetChanged() // Memberitahukan RecyclerView bahwa data telah diperbarui
    //}

    // Mengembalikan catatan pada posisi tertentu
    //fun getNoteAt(position: Int): Note {
    //    return notes[position]
    //}

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
