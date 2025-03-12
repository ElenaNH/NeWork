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

    @Singleton
    @Provides
    fun provideApiService(
        retrofit: Retrofit
    ): AuthRegApiService = retrofit.create()

}
