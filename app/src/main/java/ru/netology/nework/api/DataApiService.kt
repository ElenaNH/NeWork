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
import ru.netology.nework.dto.Event
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.Post
import kotlin.Long


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

    // ***************
    // ПОСТЫ и СОБЫТИЯ
    // ***************

    // Список всех событий
    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>

    // Событие по id
    @GET("events/{id}")
    suspend fun getEventById(id: Long): Response<Event>

    // Список всех постов
    @GET("posts")
    suspend fun getAllPosts(): Response<List<Post>>

    // Пост по id
    @GET("post/{id}")
    suspend fun getPostById(id: Long): Response<Post>

    // Список "моих" постов (текущего пользователя)
    @GET("my/wall")
    suspend fun getMyWall(): Response<List<Post>>

    // Пост по id (если он при этом "мой")
    @GET("my/wall/{id}")
    suspend fun getMyPostById(id: Long): Response<Post>

    // Список постов заданного пользователя
    @GET("{authorId}/wall")
    suspend fun getUserWall(authorId: Long): Response<List<Post>>

    // Пост по id (только если это пост заданного пользователя)
    @GET("{authorId}/wall/{id}")
    suspend fun getUserPostById(authorId: Long, id: Long): Response<List<Post>>

}


