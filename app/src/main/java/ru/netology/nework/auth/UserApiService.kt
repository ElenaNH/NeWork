package ru.netology.nework.auth

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import ru.netology.nework.BuildConfig
import ru.netology.nework.dto.Token


private const val BASE_URL_SERVICE = "http://94.228.125.136:8080/api/"

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
        AppAuth.getInstance().data.value?.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return@addInterceptor chain.proceed(newRequest)
        }
        chain.proceed(chain.request())
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL_SERVICE)
    .client(okhttp)
    .build()


interface UserApiService {

    // Запросы авторизации

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    /*   // Запросы регистрации (получение токена регистрации)

       @FormUrlEncoded
       @POST("users/registration")
       suspend fun registerUser(
           @Field("login") login: String,
           @Field("pass") pass: String,
           @Field("name") name: String
       ): Response<Token>

       @Multipart
       @POST("users/registration")
       suspend fun registerWithPhoto(
           @Part("login") login: String,
           @Part("pass") pass: String,
           @Part("name") name: String,
           @Part media: MultipartBody.Part? = null,
       ): Response<Token>

   //        @Multipart
   //        @POST("users/registration")
   //        suspend fun registerWithPhoto(
   //            @Part("login") login: RequestBody,
   //            @Part("pass") pass: RequestBody,
   //            @Part("name") name: RequestBody,
   //            @Part media: MultipartBody.Part,
   //        ): Response<Token>


       @FormUrlEncoded
       @POST("users/{id}")
       suspend fun getUser(
           @Field("login") login: String,
           @Field("pass") pass: String,
           @Field("name") name: String
       ): Response<User>

       // Отправка push-токена
       @POST("users/push-tokens")
       suspend fun sendPushToken(@Body token: PushToken): Response<Unit>
   */

}

object UserApi {
    val retrofitService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}

