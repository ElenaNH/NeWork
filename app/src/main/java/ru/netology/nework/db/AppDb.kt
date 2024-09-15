package ru.netology.nework.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.nework.dao.AppDao
import ru.netology.nework.entity.AuthEntity
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.UserJobEntity
import ru.netology.nework.entity.UserListTypeEntity

// Объявляем AppDb - абстрактный класс
// Его имплементацию сгенерит библиотека ROOM сама
// Мы только должны указать в аннотации все Entity-классы (структуры всех таблиц)
// В теле класса нужно указать правильный Dao-класс
@Database(
    entities = [
        AuthEntity::class,
        UserEntity::class,
        UserJobEntity::class,
        UserListTypeEntity::class,
        /*        NoteEntity::class,
                NoteUserListsEntity::class*/
    ],
    version = 1
)
abstract class AppDb : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
