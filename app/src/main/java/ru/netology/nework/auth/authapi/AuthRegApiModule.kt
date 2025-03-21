package ru.netology.nework.auth.authapi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nework.BuildConfig
import ru.netology.nework.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthRegApiModule {

    companion object {
        //const val BASE_URL = BuildConfig.BASE_URL
        private const val BASE_URL_SERVICE = "$BASE_URL/api/"

    }

// НАЧАЛО КУСКА, который НЕ ДОЛЖЕН повторяться в DataApiModule

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        logging: HttpLoggingInterceptor,
        appAuth: AppAuth
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            appAuth.data.value?.token?.let { token ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
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
        }
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL_SERVICE)
        .client(okHttpClient)
        .build()

// КОНЕЦ КУСКА, который НЕ ДОЛЖЕН повторяться в DataApiModule


    @Singleton
    @Provides
    fun provideAuthRegApiService(
        retrofit: Retrofit
    ): AuthRegApiService = retrofit.create()

}
