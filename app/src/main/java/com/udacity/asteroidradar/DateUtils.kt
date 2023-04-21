package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

        companion object {
            fun format(date: Date, format: String, locale: Locale = Locale.getDefault()): String {
                val formatter = SimpleDateFormat(format, locale)
                return formatter.format(date)
            }

            fun addDays(date: Date, daysToAdd: Int): Date {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
                return calendar.time
            }
        }


}