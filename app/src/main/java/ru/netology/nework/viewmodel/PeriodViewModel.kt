package ru.netology.nework.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nework.R

class PeriodViewModel(application: Application) : AndroidViewModel(application) {
    val period = MutableLiveData(Period())
    private val appContext = application.baseContext

    fun setPeriod(dateStart: String, dateFinish: String) {
        period.value = Period(dateStart, dateFinish)
    }

    inner class Period(val dateStart: String = "", val dateFinish: String = "") {
        override fun toString(): String {
            return if (dateStart.isEmpty() && dateFinish.isEmpty()) ""
            else "${convertEmpty(dateStart)} - ${convertEmpty(dateFinish)}"
        }

        private fun convertEmpty(str: String): String {
            return if (str.trim().isEmpty())
                try {
                    appContext.getString(R.string.present)
                } catch (e: Exception) {
                    ""
                }
            else str.trim()
        }
    }
}

