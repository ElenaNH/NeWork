package ru.netology.nework.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.netology.nework.auth.authapi.BASE_URL
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataApiModule {

    companion object {
        //const val BASE_URL = BuildConfig.BASE_URL
        private const val BASE_URL_SERVICE = "$BASE_URL/api/"

    }

 /* В AuthRegApiModule в этом месте есть дополнительный кусок кода
 * Сюда его результат retrofit будет просто подставлен как синглтон
 * Также можно было объединить два модуля в один CommonApiModule
 * И только было было бы две функции
 * fun provideDataApiService() и fun provideAuthRegApiService() */

    @Singleton
    @Provides
    fun provideDataApiService(
        retrofit: Retrofit
    ): DataApiService = retrofit.create()


}
