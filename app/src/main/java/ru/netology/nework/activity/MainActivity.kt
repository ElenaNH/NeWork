package ru.netology.nework.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.api.UserApi
import ru.netology.nework.auth.dto.Token
import ru.netology.nework.auth.dto.UserResponse
import java.text.SimpleDateFormat
import java.util.Date


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
    //var lastUserLogin = "User" // Логин не хранили в shared preferences,а только токен
    var lastUserName = "User"  // Нет смысла делать тут lateinit конкретно в этом коде

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val textMessage = findViewById<TextView>(R.id.message)
        val buttonRegister = findViewById<Button>(R.id.register)
        val buttonLogon = findViewById<Button>(R.id.logon)
        val buttonLogoff = findViewById<Button>(R.id.logoff)

        lastUserName = auth.currentUser.value?.name ?: "User"

        //Получаем данные об аутентификации  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            auth.data.collectLatest {
                textMessage.text =
                    "${testCountPositive} authorisation(s). " +
                            "Hello, ${if (it == null) "Anonymous" else lastUserName}!\n"
                Log.d("CALLED","auth.data.collectLatest")
            }
        }
        //Получаем данные о текущем пользователе  //lifecycleScope.launchWhenCreated
        lifecycleScope.launch {
            auth.currentUser.collectLatest {
                textMessage.text =
                    "${testCountPositive} authorisation(s). " +
                            "Hello, ${if (it == null) "Anonymous" else it.name}!\n"
                Log.d("CALLED","auth.currentUser.collectLatest")
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
        // lastUserLogin = userLogin
        lastUserName = auth.currentUser.value?.name ?: "User"

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
        // lastUserLogin = userLogin
        lastUserName = auth.currentUser.value?.name ?: "User"

        // Надо прогрузить токен в auth
        auth.setToken(
            responseToken,
        )


        // ==========================================================
        // Теперь запросим всю отображаемую информацию о пользователе
        var responseUserInf: Response<UserResponse>? = null
        try {

            responseUserInf = apiService.getUserById(
                responseToken.id,
            )

        } catch (e: Exception) {
            // Обычно сюда попадаем, если нет ответа сервера
            throw RuntimeException("Server response failed: ${e.message.toString()}")
        }

        if 	(!(responseUserInf.isSuccessful ?: false)) {
            val responseUserInfStatus = responseUserInf.code()
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            val errText =
                responseUserInfStatus.toString() + ": " + if (responseUserInf.message() == "")
                    "No server response" else responseUserInf.message()
            throw RuntimeException("Request declined: $errText")
        }
        val userResponse = responseUserInf.body() ?: throw RuntimeException("body is null")

        // Надо прогрузить текущего пользователя в auth
        val sdf = SimpleDateFormat("mmss")  //"dd/M/yyyy hh:mm:ss"
        val currMinSec = sdf.format(Date())

        auth.setCurrentUser(
            userResponse //userResponse.copy(name = userResponse.name + "_" + currMinSec),
        )

    }

}
