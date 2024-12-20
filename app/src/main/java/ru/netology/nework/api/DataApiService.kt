package ru.netology.nework.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nework.auth.authapi.BASE_URL
import ru.netology.nework.auth.authapi.okhttp
import ru.netology.nework.dto.Media
import ru.netology.nework.auth.authdto.UserResponse

//const val BASE_URL = BuildConfig.BASE_URL
private const val BASE_URL_SERVICE = "$BASE_URL/api/"

private val _okhttp = okhttp        // Берем из сервиса авторизации - ТОТ САМЫЙ!

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL_SERVICE)
    .client(_okhttp)
    .build()

interface DataApiService {

    // Данные пользователя по id (здесь тоже можем запросить, как и при авторизации)
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserResponse>

    // Список данных всех пользователей TODO - стоит ли запрашивать всех? или по 50-100? ЗДЕСЬ ЛИ?
    @GET("users")
    suspend fun getAllUsers(): Response<List<UserResponse>>

    @Multipart
    @POST("media")
    suspend fun saveAvatarMedia(@Part part: MultipartBody.Part): Response<Media>

    /*
           // Отправка push-токена
           @POST("users/push-tokens")
           suspend fun sendPushToken(@Body token: PushToken): Response<Unit>
       */

}

object DataApi {
    val retrofitService: DataApiService by lazy {
        retrofit.create(DataApiService::class.java)
    }
}

