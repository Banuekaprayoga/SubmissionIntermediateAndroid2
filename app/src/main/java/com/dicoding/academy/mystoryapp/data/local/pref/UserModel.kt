package com.dicoding.academy.mystoryapp.data.local.pref

data class UserModel(
    val name: String,
    val email: String,
    val password: String,
    val token: String,
    val isLogin: Boolean = false
)