package ru.netology.nework.util

import android.util.Log
import java.text.SimpleDateFormat

// Формат хранения отличается от формата отображения
private val visiblePattern = "MM/dd/yyyy"
private val storedPattern = "yyyy-MM-dd"

val visibleDateFormat = SimpleDateFormat(visiblePattern)
val storedDateFormat = SimpleDateFormat(storedPattern)


public fun visibleDateToStored(strVisible: String, endOfDay: Boolean = false): String {
    if (strVisible.isEmpty()) return ""
    var result = ""
    try {
        result = buildString {
            append(storedDateFormat.format(visibleDateFormat.parse(strVisible)))
            append(if (endOfDay) "T23:59:59.000Z" else "T00:00:01.000Z")
        }
    } catch (e: RuntimeException) {
        Log.e("visibleDateToStored", e.toString())
        // TODO Позже удалим выброс ошибки. Тогда просто вернется пустая строка
        throw e
    }
    return result
}

public fun storedDateToVisible(strStored: String): String {
    var result = strBeforeT(strStored)
    if (result.isEmpty()) return ""
    try {
        result = visibleDateFormat.format(storedDateFormat.parse(strStored))
    } catch (e: RuntimeException) {
        Log.e("storedDateToVisible", e.toString())
        // TODO Позже удалим выброс ошибки. Тогда просто вернется пустая строка
        throw e
    }
    return result
}

private fun strBeforeT(strStored: String): String {
    return if (strStored.contains('T'))
        strStored.substringBefore("T")
    else ""
}
