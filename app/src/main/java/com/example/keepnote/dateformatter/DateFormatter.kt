package com.example.keepnote.dateformatter

import  java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    fun formatToIndonesiaDayDate(date: Long): String{
        val locale = Locale("id", "ID") // mengatur lokal ke  bahasa indonesia
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM YYYY", locale ) // menggunakan pola untuk hari dan tanggal
        return  dateFormat.format(Date(date)) // mengembalikan hasil format
    }
}