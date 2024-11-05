package com.example.keepnote.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.keepnote.entity.Trash

// Data Access Object (DAO) untuk operasi database pada tabel trash
@Dao
interface TrashDao {

    // Menyisipkan catatan yang dihapus ke dalam tabel trash yang berada pada halaman riwayat hapus
    @Insert
    suspend fun insert(trash: Trash)

    // Mengambil semua catatan yang ada di dalam tabel trash dan mengembalikannya sebagai LiveData
    @Query("SELECT * FROM trash")
    fun getAllTrash(): LiveData<List<Trash>> // Menggunakan LiveData untuk perubahan otomatis di UI

    // Menghapus catatan dari tabel trash berdasarkan ID (menghapus satu baris data dari tabel trash)
    @Query("DELETE FROM trash WHERE id = :trashId")
    suspend fun deleteById(trashId: Long)

    // Menghapus catatan secara permanen dari tabel trash berdasarkan ID
    // Mirip dengan deleteById, tetapi biasanya digunakan untuk konfirmasi penghapusan permanen
    @Query("DELETE FROM trash WHERE id = :trashId")
    suspend fun permanentlyDelete(trashId: Long)

    // Mengambil semua catatan dari tabel trash sebagai array
    @Query("SELECT * FROM trash")
    fun getAll(): Array<Trash>
}