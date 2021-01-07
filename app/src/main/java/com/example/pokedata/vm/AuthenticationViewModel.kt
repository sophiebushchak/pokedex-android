package com.example.pokedata.vm

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.firebase.Authentication
import kotlinx.coroutines.launch

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    private val authentication: Authentication = Authentication()

    val authenticationError: LiveData<String> get() = authentication.error

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    val isLoggedIn: LiveData<Boolean> get() = authentication.loginStatus

    val loginSuccess: LiveData<String?> get() = authentication.loginSuccess
    val createUserSuccess: LiveData<String?> get() = authentication.createUserSuccess

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                if (validateFieldsLogin(email, password)) {
                    authentication.loginUser(email, password)
                }
            } catch (exception: Throwable) {
                println(exception)
            }
        }
    }

    fun createUser(email: String, password: String, passwordConfirm: String) {
        viewModelScope.launch {
            try {
                if (validateFieldsSignup(email, password, passwordConfirm)) {
                    authentication.createUser(email, password)
                }
            } catch (exception: Throwable) {
                println(exception)
            }
        }
    }

    private fun validateFieldsLogin(
        email: String?,
        password: String?,
    ): Boolean {
        println("$email, $password")
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            _error.value = "Please fill in all fields."
            return false
        }
        if (!email.isEmailValid()) {
            _error.value = "Please enter a valid email."
            return false;
        }
        if (password.length < 6) {
            _error.value = "Password should be 6 characters or more."
            return false
        }
        if (password.length > 24) {
            _error.value = "Password should be 24 characters or less."
            return false
        }
        return true
    }

    private fun validateFieldsSignup(email: String?, password: String?, passwordConfirm: String?): Boolean {
        if (!validateFieldsLogin(email, password)) {
            return false
        }
        if (passwordConfirm != password) {
            _error.value = "Password confirmation does not match password."
            return false
        }
        return true
    }

    private fun String.isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}

