package ru.netology.nework.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nework.api.DataApiService
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
import ru.netology.nework.exept.AlertAuthorizationRequiredException
import ru.netology.nework.exept.AlertServerAccessingErrorException
import ru.netology.nework.exept.AlertWrongServerResponseException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val appDao: AppDao,
    private val dataApiService: DataApiService,
) : UserRepository {
//class UserRepositoryImpl(private val appDao: AppDao) : UserRepository {

    override val data: Flow<List<User>> =
        /*get() =*/ appDao.getAllUsers()
        .map { it.toLocalDto() }
        .flowOn(Dispatchers.Default)

    override suspend fun fillInitial() {

        // TODO - это тестирование! Нужно перенести его на момент создания БД
        UserListType.entries.forEach { entry ->
            appDao.initialUserFilling(
                UserListTypeEntity(
                    entry.code,
                    entry.toString(),
                    entry.marker
                )
            )
        }

    }

    override suspend fun getAllUsers(): List<User> {

        // Запросим список постов с сервера
        val response = dataApiService.getAllUsers()
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
            appDao.insertUser(testingSave)

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

        // Запросим список работ пользователя с сервера
        val response = dataApiService.getJobsByUserId(userId)
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

            appDao.insertUserJobs(dataForSave)
        } else {
            appDao.clearJobsByUserId(userId)
        }

        val jobEntities = appDao.getJobsByUserId(userId)
        return jobEntities.let(List<UserJobEntity>::toDto)
    }

    override suspend fun removeJob(id: Long) {
        // отправим запрос на удаление, после чего заново получим и перегрузим все работы пользователя

        // Отправим запрос удаления на сервер
        val response = dataApiService.deleteMyJob(id)
        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (response.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                403 -> throw AlertAuthorizationRequiredException()
                else -> throw AlertWrongServerResponseException(
                    response.code(),
                    response.message()
                )
            }
        }
        val responseUnit = response?.body() ?: throw AlertWrongServerResponseException(
            response.code(),
            "body is null"
        )
        // Если мы тут, то сервер вернул ожидаемый Unit,а не null
        // тогда удалим запись из БД
        appDao.removeJobById(id)
    }

    override suspend fun saveJob(job: Job): Job {
        // отправим запрос на сервер
        Log.d("step 0", "Before server access")
        val response =
            try {
                dataApiService.saveMyJob(job)
            } catch (e: Exception) {
                Log.e("Server access error: ", e.toString())
                throw AlertServerAccessingErrorException(e.toString())
            }
        Log.d("step 1", "After server access")
        if (!(response?.isSuccessful ?: false)) {
            // А сюда попадаем, потому что сервер вернул isSuccessful == false
            when (response.code()) {
                //200 -> true // Сюда не должны попасть из-за верхней проверки
                403 -> throw AlertAuthorizationRequiredException()
                else -> throw AlertWrongServerResponseException(
                    response.code(),
                    response.message()
                )
            }
        }
        val responseJob = response?.body() ?: throw AlertWrongServerResponseException(
            response.code(),
            "body is null"
        )
        Log.d("step 2", "Server response received")
        // Если мы тут, то сервер вернул ожидаемый Job,а не null
        // тогда добавим либо обновим запись в БД
        if (job.id != responseJob.id) {
            // сервер присвоил другой id, значит, нужно удалить из БД, если там была эта временная запись
            appDao.removeJobById(job.id)
        }
        // Теперь добавим либо обновим пришедшую с сервера запись в БД (и вернем её же)
        val jobEntity = UserJobEntity.fromDto(responseJob).copy(userId = job.userId)
        appDao.insertUserJob(jobEntity)

        Log.d("INSERTED jobEntity", jobEntity.toString())
        return jobEntity.toDto()
    }

}
