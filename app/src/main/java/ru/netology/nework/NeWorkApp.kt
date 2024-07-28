package ru.netology.nework

import android.app.Application
import ru.netology.nework.auth.AppAuth

class NeWorkApp : Application() {
// Чтобы у этого класса была связь с процессом, его нужно зарегистрировать в манифесте

    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}
