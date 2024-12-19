package com.dicoding.academy.mystoryapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.academy.mystoryapp.data.local.pref.UserModel
import com.dicoding.academy.mystoryapp.data.local.pref.UserPreference
import com.dicoding.academy.mystoryapp.data.remote.response.DetailStoryResponse
import com.dicoding.academy.mystoryapp.data.remote.response.FileUploadResponse
import com.dicoding.academy.mystoryapp.data.remote.response.ListStoryItem
import com.dicoding.academy.mystoryapp.data.remote.response.ListStoryResponse
import com.dicoding.academy.mystoryapp.data.remote.response.LoginResponse
import com.dicoding.academy.mystoryapp.data.remote.response.RegisterResponse
import com.dicoding.academy.mystoryapp.data.remote.response.Story
import com.dicoding.academy.mystoryapp.data.remote.retrofit.ApiConfig
import com.dicoding.academy.mystoryapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            val token = response.loginResult?.token
            ApiConfig.updateToken(token)
            saveSession(
                UserModel(
                    name = response.loginResult?.name ?: "",
                    email = email,
                    password = password,
                    token = response.loginResult?.token ?: "",
                    isLogin = true
                )
            )
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }
    fun registerUser(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun logout() {
        ApiConfig.updateToken(null)
        userPreference.logout()
    }
    fun getSession(): Flow<UserModel> = userPreference.getSession().onEach { user ->
        ApiConfig.updateToken(user.token.takeIf { user.isLogin })
    }

    fun uploadFile(file: MultipartBody.Part, desc: RequestBody): LiveData<Result<FileUploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadImage(file, desc)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, FileUploadResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }
    fun getDetailStory(id: String): LiveData<Result<Story>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory(id)
            val detailStory = response.story
            emit(Result.Success(detailStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, DetailStoryResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }
    fun getStoryList(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 25
            ),
            pagingSourceFactory = {
                StoryListPagingSource(apiService)
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            val storyListLocation = response.listStory
            emit(Result.Success(storyListLocation))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ListStoryResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}