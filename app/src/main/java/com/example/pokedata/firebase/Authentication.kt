package com.example.pokedata.firebase

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext
import java.lang.Exception

class Authentication() {
    private val TAG = "FIREBASE_AUTHENTICATION"
    private var authentication: FirebaseAuth = Firebase.auth

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _createUserSuccess = MutableLiveData<String>()
    val createUserSuccess: LiveData<String> get() = _createUserSuccess

    private val _loginSuccess = MutableLiveData<String>()
    val loginSuccess: LiveData<String> get() = _loginSuccess

    fun isLoggedIn(): Boolean {
        val user = authentication.currentUser
        return user != null
    }

    fun createUser(email: String, password: String) {
            authentication.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUser:success")
                        _createUserSuccess.value = "Successfully created account with email $email"
                        loginUser(email, password)
                    } else {
                        Log.w(TAG, "createUser:failure", task.exception)
                        _error.value = "Could not create user. The account possibly already exists."
                    }
                }
    }

    fun loginUser(email: String, password: String) {
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "loginUser:success")
                    _loginSuccess.value = "Successfully logged into account with email $email"
                } else {
                    Log.w(TAG, "loginUser:failure", task.exception)
                    _error.value = "Could not sign in. Account either does not exist or password was incorrect."
                }
            }
    }

    fun getCurrentUser(): FirebaseUser {
        if (!isLoggedIn()) {
            throw AuthenticationException("Not logged in!")
        } else {
            //Checked if logged in, so user is guaranteed to be present
            return Firebase.auth.currentUser!!
        }
    }

    inner class AuthenticationException(message: String) : Exception(message)
}
