package com.dicoding.academy.mystoryapp.di

import android.content.Context
import com.dicoding.academy.mystoryapp.data.UserRepository
import com.dicoding.academy.mystoryapp.data.local.pref.UserPreference
import com.dicoding.academy.mystoryapp.data.local.pref.dataStore
import com.dicoding.academy.mystoryapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}