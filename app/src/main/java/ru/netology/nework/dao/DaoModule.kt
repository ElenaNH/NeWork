package ru.netology.nework.dao

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nework.db.AppDb

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun provideAppDao(db:AppDb): AppDao = db.appDao()

/*    @Provides
    fun ProvidePostRemoteKeyDao(db:AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()*/
}
