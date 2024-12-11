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

    class TrashViewHolder(private val binding: TrashItemBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val binding = TrashItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrashViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val trash = getItem(position)
        holder.bind(trash, onRecoverClick, onDeleteClick)
    }

    class TrashDiffCallback : DiffUtil.ItemCallback<Trash>() {
        override fun areItemsTheSame(oldItem: Trash, newItem: Trash): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Trash, newItem: Trash): Boolean = oldItem == newItem
    }


}
