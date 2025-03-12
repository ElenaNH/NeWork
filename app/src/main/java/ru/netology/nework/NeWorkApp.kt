package ru.netology.nework

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NeWorkApp : Application() {
// Чтобы у этого класса была связь с процессом, его нужно зарегистрировать в манифесте

/*  // Мы уже создаем appAuth с помощью Hilt, так что здесь удаляем создание
        override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)

    }*/

}
