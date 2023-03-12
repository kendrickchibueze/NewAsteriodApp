package com.example.newasteriodapp

import java.text.SimpleDateFormat
import java.util.*

class MyUtils private constructor() {

    companion object {
        fun DateStringConversion(date: Date, format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(date)
        }

        fun addDaysToDate(date: Date, daysToAdd: Int): Date {
            val c = Calendar.getInstance().apply { time = date }
            c.add(Calendar.DATE, daysToAdd)
            return c.time
        }
    }

}

