package ru.netology.nework.auth.authapi

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nework.BuildConfig
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.auth.authdto.Token
import ru.netology.nework.auth.authdto.UserResponse

const val BASE_URL = BuildConfig.BASE_URL
private const val BASE_URL_SERVICE = "$BASE_URL/api/"

/*
private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}


private val _okhttpAuth = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
        AppAuth.getInstance().data.value?.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)  // Авторизация пользователя
                .build()
            return@addInterceptor chain.proceed(newRequest)
        }
        chain.proceed(chain.request())
    }
    .addInterceptor { chain ->
        chain.proceed(
            chain.request().newBuilder()
                .addHeader(
                    "Api-Key",
                    BuildConfig.SERVER_API_KEY
                ) // Разработческий ключ доступа к серверу
                .build()
        )
    }.build()

// Попробуем общий okhttp для всех сервисов
val okhttpAuth: OkHttpClient
    get() = _okhttpAuth

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL_SERVICE)
    .client(_okhttpAuth)
    .build()*/

interface AuthRegApiService {

    // Запросы авторизации

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    // Запросы регистрации (получение токена регистрации)
    // без фото
    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<Token>

    // с фото
    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part login: MultipartBody.Part,
        @Part pass: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part media: MultipartBody.Part? = null,
    ): Response<Token>

    // Данные пользователя по id (это нужно сразу после авторизации, а остальное уходит в другой сервис)
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserResponse>


    /*
           // Отправка push-токена
           @POST("users/push-tokens")
           suspend fun sendPushToken(@Body token: PushToken): Response<Unit>
       */

}

/*object AuthRegApi {
    val retrofitService: AuthRegApiService by lazy {
        retrofit.create(AuthRegApiService::class.java)
    }
}*/

