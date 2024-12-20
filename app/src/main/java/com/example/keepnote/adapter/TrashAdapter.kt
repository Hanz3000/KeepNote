package com.example.keepnote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.databinding.TrashItemBinding
import com.example.keepnote.dateformatter.DateFormatter
import com.example.keepnote.entity.Trash

class TrashAdapter(
    private val onRecoverClick: (Trash) -> Unit, // Fungsi untuk memulihkan catatan
    private val onDeleteClick: (Trash) -> Unit  // Fungsi untuk menghapus catatan permanen
) : ListAdapter<Trash, TrashAdapter.TrashViewHolder>(TrashDiffCallback()) {

    // ViewHolder yang menghubungkan data sampah (Trash) ke item tampilan RecyclerView menggunakan TrashItemBinding
    class TrashViewHolder(private val binding: TrashItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Fungsi untuk mengikat data Trash ke tampilan
        fun bind(trash: Trash, onRecoverClick: (Trash) -> Unit, onDeleteClick: (Trash) -> Unit) {
            with(binding) {
                // Isi data dengan properti dari Trash
                textViewTitle.text = trash.title ?: "Judul Tidak Diketahui"
                textViewContent.text = trash.content ?: "Konten Tidak Diketahui"
                textViewDeletedDate.text = "Dihapus pada ${DateFormatter.formatToIndonesiaDayDate(trash.deletedDate)}"
                textViewCategory.text = trash.category?.let { "Kategori: $it" } ?: "Kategori: Tidak Diketahui"

                // Tombol untuk memulihkan catatan
                buttonRestore.setOnClickListener { onRecoverClick(trash) }
                // Tombol untuk menghapus catatan secara permanen
                buttonDelete.setOnClickListener { onDeleteClick(trash) }

            }
        }
    }

    // Membuat TrashViewHolder baru dengan layout TrashItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val binding = TrashItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrashViewHolder(binding)
    }

    // Mengikat data Trash ke TrashViewHolder sesuai posisi di RecyclerView
    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val trash = getItem(position)
        holder.bind(trash, onRecoverClick, onDeleteClick)
    }

    // Membandingkan item berdasarkan ID dan konten untuk pembaruan ListAdapter
    class TrashDiffCallback : DiffUtil.ItemCallback<Trash>() {
        override fun areItemsTheSame(oldItem: Trash, newItem: Trash): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Trash, newItem: Trash): Boolean = oldItem == newItem
    }


}
