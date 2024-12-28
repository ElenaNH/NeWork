package ru.netology.nework.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.DataApi
import ru.netology.nework.dao.AppDao
import ru.netology.nework.dto.Job
import ru.netology.nework.dto.User
import ru.netology.nework.entity.UserEntity
import ru.netology.nework.entity.UserJobEntity
import ru.netology.nework.entity.UserListTypeEntity
import ru.netology.nework.entity.fromRemoteDto
import ru.netology.nework.entity.toDto
import ru.netology.nework.entity.toEntity
import ru.netology.nework.entity.toLocalDto
import ru.netology.nework.enumeration.UserListType
import ru.netology.nework.exept.AlertWrongServerResponseException

class UserRepositoryImpl(private val appDao: AppDao) : UserRepository {

    override val data: Flow<List<User>> =
        /*get() =*/ appDao.getAllUsers()
        .map { it.toLocalDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun fillInitial() {

        // TODO - это тестирование! Нужно перенести его на момент создания БД
        UserListType.entries.forEach { entry ->
            appDao.initialFilling(UserListTypeEntity(entry.code, entry.toString(), entry.marker))
        }

    }

    override suspend fun getAllUsers(): List<User> {

        // Запросим список постов с сервера
        val response = DataApi.retrofitService.getAllUsers()
        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (response.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                else -> throw AlertWrongServerResponseException(
                    response.code(),
                    response.message()
                )
            }
        }
        val receivedUserResponseList = response?.body() ?: throw AlertWrongServerResponseException(
            response.code(),
            "body is null"
        )

        // Непустой список пользователей сохраняем в БД
        if (receivedUserResponseList.count() > 0) {
            val testingSave = receivedUserResponseList.fromRemoteDto()
            appDao.saveUser(testingSave)

            /*appDao.saveUser(receivedUserResponseList.fromRemoteDto())*/
        }

        val testingGet = appDao.getAllUsersAlternate()
        val testingConvert = testingGet.let(List<UserEntity>::toLocalDto)
        return testingConvert

        // После вставки возвращаем преобразованные к локальному ui-формату данные
        // Вернется не только то, что вставилось, но вообще все, что есть в БД
        /*return appDao.getAllUsersAlternate()
            .let(List<UserEntity>::toLocalDto)*/
    }

    override suspend fun getUserById(id: Long): User? {
        return appDao.getUserById(id)
            .firstOrNull()
            .let { it?.toLocalDto() }
    }

    override suspend fun getUserJobsById(userId: Long): List<Job> {

        // TODO - сделать по аналогии с getAllUsers()

        // Запросим список работ пользователя с сервера
        val response = DataApi.retrofitService.getJobsByUserId(userId)
        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (response.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                else -> throw AlertWrongServerResponseException(
                    response.code(),
                    response.message()
                )
            }
        }
        val receivedJobList = response?.body() ?: throw AlertWrongServerResponseException(
            response.code(),
            "body is null"
        )

        // Непустой список работ пользователя сохраняем в БД (предварительно прогрузим туда userId)
        if (receivedJobList.count() > 0) {
            val dataForSave = receivedJobList
                .map { it.copy(userId = userId) }
                .toEntity()

            // Изначально удаляем список работ данного пользователя, у которых id отсутствуют в новом списке
            // (Если все удалять перед вставкой, то рискуем получить пустой список на секунду при рассинхроне)
            val dataForDelete = appDao.getJobsByUserId(userId)
                .filterNot { datumDel -> dataForSave.count { it.id == datumDel.id } > 0 }
            dataForDelete.forEach { appDao.removeJobById(it.id) }

            appDao.saveUserJobs(dataForSave)
        } else {
            appDao.clearJobsByUserId(userId)
        }

        val testingGet = appDao.getJobsByUserId(userId)
        val testingConvert = testingGet.let(List<UserJobEntity>::toDto)
        return testingConvert

        /*return appDao.getUserJobsById(userId)
            .let(List<UserJobEntity>::toDto)*/
    }

}
