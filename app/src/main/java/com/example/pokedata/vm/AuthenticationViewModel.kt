package com.example.pokedata.vm

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pokedata.App
import com.example.pokedata.R
import com.example.pokedata.firebase.Authentication
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication related functionality. This is used on the SplashFragment and
 * LoginFragment.
 */
class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    private val authentication: Authentication = Authentication()
    private val resources = App.getRes()

    val authenticationError: LiveData<String?> get() = authentication.error

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    val isLoggedIn: LiveData<Boolean> get() = authentication.loginStatus

    val loginSuccess: LiveData<String?> get() = authentication.loginSuccess
    val createUserSuccess: LiveData<String?> get() = authentication.createUserSuccess

    /**
     *  Attempt to log in a user. No LiveData values are set because the [Authentication] class already
     *  emits LiveData values.
     */
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

    /**
     *  Attempt to sign in a user. No LiveData values are set because the [Authentication] class already
     *  emits LiveData values.
     */
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

    /**
     * Validate the login form fields and notify the user of an error.
     */
    private fun validateFieldsLogin(
        email: String?,
        password: String?,
    ): Boolean {
        println("$email, $password")
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            _error.value = resources.getString(R.string.emptyFields)
            return false
        }
        if (!email.isEmailValid()) {
            _error.value = resources.getString(R.string.invalidEmail)
            return false;
        }
        if (password.length < 6) {
            _error.value = resources.getString(R.string.passwordTooShort)
            return false
        }
        if (password.length > 24) {
            _error.value = resources.getString(R.string.passwordTooLong)
            return false
        }
        return true
    }

    /**
     * Use standard login validation but also validate if the password confirm matches the password
     */
    private fun validateFieldsSignup(
        email: String?,
        password: String?,
        passwordConfirm: String?
    ): Boolean {
        if (!validateFieldsLogin(email, password)) {
            return false
        }
        if (passwordConfirm != password) {
            _error.value = resources.getString(R.string.passwordsDoNotMatch)
            return false
        }
        return true
    }

    /**
     * Extension function to check if a valid email was entered.
     */
    private fun String.isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}

