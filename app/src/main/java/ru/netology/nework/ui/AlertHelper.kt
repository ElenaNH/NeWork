package ru.netology.nework.ui

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import ru.netology.nework.exept.AlertException
import ru.netology.nework.exept.AlertInfo

typealias Rstring = ru.netology.nework.R.string

fun AlertInfo.alertImplementation(context: Context): String {
    var stringResourceFound = true
    val text = try {
        context.getString(this.rStringCode)
    } catch (e: Exception) {
        stringResourceFound = false
        Log.d("CATCH-0", "Before fun getString(alert_string_resource_not_found)")
        context.getString(Rstring.alert_string_resource_not_found)
    }

    // Никак не хочет список преобразовываться в массив
    // Пришлось в цикле делать  TODO - разобраться!

    var alertArgs: Array<Any> = arrayOf()
    if (stringResourceFound) for (elem in this.args) alertArgs += elem //alertArgs = alertArgs.plus(elem)

    val formattedAlert = String.format(
        text,
        *alertArgs
    )

    return formattedAlert
}

fun AlertException.alertImplementation(context: Context) =
    this.alertInfo().alertImplementation(context)

fun Context.showToast(textInformation: String) {
    val warnToast = Toast.makeText(
        this,
        textInformation,
        Toast.LENGTH_SHORT
    )
    warnToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
    warnToast.show()
}


