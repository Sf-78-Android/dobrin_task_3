package com.training.weatherapp.utils

import android.annotation.SuppressLint
import com.training.weatherapp.constatns.Constants
import java.text.SimpleDateFormat
import java.util.*

object Util {

     fun getUnit(value: String): String {
        if (Constants.US_SYMBOL == value || Constants.LR_SYMBOL == value || Constants.MM_SYMBOL == value) {
            return Constants.FAHRENHEIT_SYMBOL
        }
        return Constants.CELSIUS_SYMBOL

    }

     fun unixTime(timex: Long): String? {
        val date = Date(timex * Constants.DATE_ALLOCATION)

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT)
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}