package ru.netology.nework.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.netology.nework.BuildConfig
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.api.UserApi
import ru.netology.nework.auth.dto.Token


class MainActivity : AppCompatActivity() {

    val auth: AppAuth by lazy {
        AppAuth.initApp(this)
        AppAuth.getInstance()
    }

    val editLogin by lazy { findViewById<EditText>(R.id.login) }
    val editPassword by lazy { findViewById<EditText>(R.id.password) }

    val tokenTest = Token(
        79,
        "79"
    )
    var testCountPositive = 0
    var lastUserLogin = "User" // Логин не хранили в shared preferences,а только токен

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val textMessage = findViewById<TextView>(R.id.message)
        val buttonRegister = findViewById<Button>(R.id.register)
        val buttonLogon = findViewById<Button>(R.id.logon)
        val buttonLogoff = findViewById<Button>(R.id.logoff)


        //Получаем данные об аутентификации  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            auth.data.collectLatest {
                textMessage.text =
                    "${testCountPositive} authorisation(s). " +
                            "Hello, ${if (it == null) "Anonymous" else lastUserLogin}!\n"
            }
        }


        buttonLogon.setOnClickListener {
            lifecycleScope.launch {
                updateUser()
            }
        }

        buttonRegister.setOnClickListener {
            lifecycleScope.launch {
                registerUser()
            }
        }

        buttonLogoff.setOnClickListener {
            auth.clearAuth()
        }


    }


    private suspend fun registerUser() {
        val apiService = UserApi.retrofitService

        val userLogin = editLogin.text.toString()
        val userPasswod = editPassword.text.toString()

        var response: Response<Token>? = null
        try {

            response = apiService.registerUser(
                userLogin,
                userPasswod,
                userLogin, // Повторяем здесь для теста
            )

        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }

        if (!(response.isSuccessful ?: false)) {
            val responseStatus = response.code()
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            val errText =
                responseStatus.toString() + ": " + if (response.message() == "")
                    "No server response" else response.message()
            throw RuntimeException("Request declined: $errText")
        }
        val responseToken = response.body() ?: throw RuntimeException("body is null")

        // Счетчик положительных ответов сервера (тестирование)
        // должен увеличиться ДО установки токена
        // (иначе обработка Flow будет со старыми данными счетчика)
        testCountPositive++
        lastUserLogin = userLogin

        // Надо прогрузить токен в auth
        auth.setToken(
            responseToken,
        )

    }

    private suspend fun updateUser() {
        val apiService = UserApi.retrofitService

        val userLogin = editLogin.text.toString()
        val userPasswod = editPassword.text.toString()

        var response: Response<Token>? = null
        try {

            response = apiService.updateUser(
                userLogin,
                userPasswod
            )

        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }

        if (!(response.isSuccessful ?: false)) {
            val responseStatus = response.code()
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            val errText =
                responseStatus.toString() + ": " + if (response.message() == "")
                    "No server response" else response.message()
            throw RuntimeException("Request declined: $errText")
        }
        val responseToken = response.body() ?: throw RuntimeException("body is null")

        // Счетчик положительных ответов сервера (тестирование)
        // должен увеличиться ДО установки токена
        // (иначе обработка Flow будет со старыми данными счетчика)
        testCountPositive++
        lastUserLogin = userLogin

        // Надо прогрузить токен в auth
        auth.setToken(
            responseToken,
        )

    }

}
