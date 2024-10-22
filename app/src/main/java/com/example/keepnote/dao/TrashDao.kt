package com.example.keepnote.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TrashDao {
    @Insert
    suspend fun insert(trash: Trash)

    @Query("SELECT * FROM trash")
    fun getAllTrash(): LiveData<List<Trash>>

    @Query("DELETE FROM trash WHERE id = :trashId")
    suspend fun deleteById(trashId: Long)

    @Query("DELETE FROM trash WHERE id = :trashId")
    suspend fun permanentlyDelete(trashId: Long)

    @Query("SELECT * FROM trash")
    fun getAll(): Array<Trash>
}