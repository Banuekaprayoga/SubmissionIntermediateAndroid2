package com.dicoding.academy.mystoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.academy.mystoryapp.data.UserRepository
import com.dicoding.academy.mystoryapp.data.local.pref.UserModel
import com.dicoding.academy.mystoryapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStoryList().cachedIn(viewModelScope)

    fun loginUser(email: String, password: String) = repository.loginUser(email, password)

    fun register(name: String, email: String, password: String) = repository.registerUser(name, email, password)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStoryLocation() = repository.getStoriesWithLocation()

    fun getDetailStory(id: String) = repository.getDetailStory(id)

    fun uploadFile(file: MultipartBody.Part, desc: RequestBody) = repository.uploadFile(file, desc)
}