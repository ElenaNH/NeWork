package ru.netology.nework.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.Token


class MainActivity : AppCompatActivity() {

    val auth: AppAuth by lazy {
        AppAuth.initApp(this)
        AppAuth.getInstance()
    }

    val tokenTest = Token(
        79,
        "79"
    )
    val userLoginTest = "79"
    val userPasswodTest = "111"
    var testCountPositive = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val textMessage = findViewById<TextView>(R.id.message)
        val buttonLogon = findViewById<Button>(R.id.logon)
        val buttonLogoff = findViewById<Button>(R.id.logoff)


        //Получаем данные об аутентификации  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            auth.data.collectLatest {

                textMessage.text =
                    "${testCountPositive} Hello, ${if (it == null) "Anonymous" else "User"}!"

            }
        }


        buttonLogon.setOnClickListener {
            lifecycleScope.launch {
                updateUser()
            }
            //auth.setToken(tokenTest)
        }

        buttonLogoff.setOnClickListener {
            auth.clearAuth()
        }


    }


    private suspend fun updateUser() {
        val apiService = ru.netology.nework.auth.UserApi.retrofitService


        var response: Response<Token>? = null
        try {
            response = apiService.updateUser(
                userLoginTest,
                userPasswodTest
            )

        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }

        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            val errText = if ((response?.message() == null) || (response?.message() == ""))
                "No server response" else response.message()
            throw RuntimeException("Request declined: $errText")
        }
        val responseToken = response?.body() ?: throw RuntimeException("body is null")

        // Надо прогрузить токен в auth
        auth.setToken(
            responseToken,
        )

        testCountPositive++  // Счетчик положительных ответов сервера (тестирование)

    }

}
