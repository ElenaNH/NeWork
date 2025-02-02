package ru.netology.nework.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nework.R

class PeriodViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.baseContext
    private val _period = MutableLiveData(Period())
    val period: LiveData<Period>
        get() = _period

    fun setPeriod(dateStart: String, dateFinish: String) {
        _period.value = Period(dateStart, dateFinish)
    }

    fun clearPeriod() {
        _period.value = Period()
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

