package com.example.pokedata.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.firebase.Authentication
import kotlinx.coroutines.launch

class AuthenticationViewModel(application: Application): AndroidViewModel(application) {
    private val authentication: Authentication = Authentication()

    val error: LiveData<String> get() = authentication.error

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    val loginSuccess: LiveData<String> get() = authentication.loginSuccess
    val createUserSuccess: LiveData<String> get() = authentication.createUserSuccess

    fun getLoggedInStatus() {
        viewModelScope.launch {
            try {
                _isLoggedIn.value = authentication.isLoggedIn()
            } catch(error: Throwable) {
                println(error)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                authentication.loginUser(email, password)
            } catch (exception: Throwable) {
                println(exception)
            }
        }
    }

    fun createUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                authentication.createUser(email, password)
            } catch(exception: Authentication.AuthenticationException) {
                println(exception)
            }
        }
    }
}