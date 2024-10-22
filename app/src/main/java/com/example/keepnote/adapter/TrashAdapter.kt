package com.example.keepnote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.keepnote.R
import com.example.keepnote.entity.Trash
import java.util.Date

class TrashAdapter(
    private var trashList: List<Trash>,
    private val onRecoverClick: (Trash) -> Unit,
    private val onDeleteClick: (Trash) -> Unit
) : RecyclerView.Adapter<TrashAdapter.TrashViewHolder>() {

    class TrashViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewContent: TextView = view.findViewById(R.id.textViewContent)
        val textViewDeletedDate: TextView = view.findViewById(R.id.textViewDeletedDate)
        val textViewCategory: TextView = view.findViewById(R.id.textViewCategory)
        val buttonRestore: Button = view.findViewById(R.id.buttonRestore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trash_item, parent, false)
        return TrashViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val trash = trashList[position]
        holder.textViewTitle.text = trash.title
        holder.textViewContent.text = trash.content
        holder.textViewDeletedDate.text = "Dihapus pada: ${Date(trash.deletedDate)}"
        holder.textViewCategory.text = "Kategori: ${trash.category}"

        holder.buttonRestore.setOnClickListener { onRecoverClick(trash) }
        holder.itemView.setOnClickListener { onDeleteClick(trash) }
    }

    override fun getItemCount(): Int = trashList.size

    fun updateData(newTrashList: List<Trash>) {
        trashList = newTrashList
        notifyDataSetChanged()
    }
}