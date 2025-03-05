package ru.netology.nework.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nework.dto.Media
import ru.netology.nework.auth.authdto.UserResponse
import ru.netology.nework.dto.Job



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

    // Список работ
    @GET("{userId}/jobs")
    suspend fun getJobsByUserId(@Path("userId") userId: Long): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveMyJob(@Body job: Job): Response<Job>
//    suspend fun saveMyJob(job: Job): Response<Job>

    @DELETE("my/jobs/{jobId}")
    suspend fun deleteMyJob(@Path("jobId") jobId: Long): Response<Unit>

    /*
           // Отправка push-токена
           @POST("users/push-tokens")
           suspend fun sendPushToken(@Body token: PushToken): Response<Unit>
       */

}

/*object DataApi {
    val retrofitService: DataApiService by lazy {
        retrofit.create(DataApiService::class.java)
    }
}*/

