package com.example.keepnote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.R
import com.example.keepnote.databinding.TrashItemBinding
import com.example.keepnote.dateformatter.DateFormatter
import com.example.keepnote.entity.Trash
import java.util.Date

// Adapter untuk RecyclerView yang menampilkan daftar catatan yang dihapus (Trash)
class TrashAdapter(
    //private var trashList: List<Trash>, // Daftar catatan yang dihapus
    private val onRecoverClick: (Trash) -> Unit, // Fungsi untuk menangani pemulihan catatan
    private val onDeleteClick: (Trash) -> Unit // Fungsi untuk menangani penghapusan permanen
) : ListAdapter<Trash, TrashAdapter.TrashViewHolder>(RowItemDiffCallback()) {

    // ViewHolder yang merepresentasikan setiap item catatan yang dihapus
    //class TrashViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    //    val textViewTitle: TextView = view.findViewById(R.id.textViewTitle) // TextView untuk judul catatan
    //    val textViewContent: TextView = view.findViewById(R.id.textViewContent) // TextView untuk isi catatan
    //    val textViewDeletedDate: TextView = view.findViewById(R.id.textViewDeletedDate) // TextView untuk tanggal penghapusan
    //    val textViewCategory: TextView = view.findViewById(R.id.textViewCategory) // TextView untuk kategori catatan
    //    val buttonRestore: Button = view.findViewById(R.id.buttonRestore) // Tombol untuk memulihkan catatan
    //}

    class TrashViewHolder(private val binding: TrashItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trash: Trash, onRecoverClick: (Trash) -> Unit, onDeleteClick: (Trash) -> Unit){
            binding.apply {
                textViewTitle.text = trash.title
                textViewContent.text = trash.content
                textViewDeletedDate.text = "Dihapus pada ${DateFormatter.formatToIndonesiaDayDate(trash.deletedDate)}"
//                textViewDeletedDate.text = "Dihapus pada ${DateFormatter.formatToIndonesianDayDate(trash.deletedDate)}"
                textViewCategory.text = "Kategori: ${trash.category}"

                buttonRestore.setOnClickListener { onRecoverClick(trash) }
                root.setOnClickListener { onDeleteClick(trash) }
            }
        }
    }

    // Dipanggil saat RecyclerView memerlukan ViewHolder baru
    //override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
    // Menginflate layout item catatan yang dihapus (trash_item.xml)
    //    val view = LayoutInflater.from(parent.context)
    //        .inflate(R.layout.trash_item, parent, false)
    //    return TrashViewHolder(view)
    //}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val binding = TrashItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrashViewHolder(binding)
    }

    // Dipanggil untuk mengikat data catatan yang dihapus ke ViewHolder
    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val trash = getItem(position)// Mendapatkan catatan yang dihapus pada posisi saat ini

        holder.bind(trash, onRecoverClick, onDeleteClick)

        // Mengatur data untuk setiap catatan yang dihapus
        //holder.textViewTitle.text = trash.title // Mengisi TextView judul dengan data catatan
        //holder.textViewContent.text = trash.content // Mengisi TextView isi catatan
        //holder.textViewDeletedDate.text = "Dihapus pada: ${Date(trash.deletedDate)}" // Mengisi tanggal penghapusan
        //holder.textViewCategory.text = "Kategori: ${trash.category}" // Mengisi kategori catatan

        // Menangani klik tombol "Pulihkan" untuk memulihkan catatan
        //holder.buttonRestore.setOnClickListener { onRecoverClick(trash) }

        // Menangani klik pada item catatan untuk menghapus permanen
        //holder.itemView.setOnClickListener { onDeleteClick(trash) }
    }

    // Mengembalikan jumlah catatan yang dihapus dalam daftar
    //override fun getItemCount(): Int = trashList.size

    // Memperbarui daftar catatan yang dihapus dengan data baru
    //fun updateData(newTrashList: List<Trash>) {
    //    trashList = newTrashList // Mengganti daftar lama dengan daftar baru
    //    notifyDataSetChanged() // Memberitahukan RecyclerView bahwa data telah berubah
    //}

    class RowItemDiffCallback : DiffUtil.ItemCallback<Trash>(){
        override fun areItemsTheSame(oldItem: Trash, newItem: Trash): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trash, newItem: Trash): Boolean {
            return oldItem == newItem
        }
    }
}