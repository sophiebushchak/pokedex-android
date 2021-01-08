package com.example.pokedata.firebase

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pokedata.App
import com.example.pokedata.R
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

class Authentication {
    private val TAG = "FIREBASE_AUTHENTICATION"
    var authentication: FirebaseAuth = Firebase.auth
    private val resources = App.getRes()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> get() = _loginStatus

    private val _createUserSuccess = MutableLiveData<String?>()
    val createUserSuccess: LiveData<String?> get() = _createUserSuccess

    private val _loginSuccess = MutableLiveData<String?>()
    val loginSuccess: LiveData<String?> get() = _loginSuccess

    /**
     * Emit livedata when the auth state is changed.
     */
    init {
        authentication.addAuthStateListener {
            _loginStatus.value = isLoggedIn()
        }
    }

    /**
     * Check if a logged in user exists
     */
    fun isLoggedIn(): Boolean {
        val user = authentication.currentUser
        return user != null
    }

    /**
     * Attempt to sign up a user by email and password.
     */
    fun createUser(email: String, password: String) {
        authentication.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUser:success")
                    _createUserSuccess.value = resources.getString(R.string.signUpSuccess, email)
                    _createUserSuccess.value = null
                    loginUser(email, password)
                } else {
                    Log.w(TAG, "createUser:failure", task.exception)
                    _error.value = resources.getString(R.string.signUpFailure)
                    _error.value = null
                }
            }
    }

    /**
     * Attempt to log in a user by email and password
     */
    fun loginUser(email: String, password: String) {
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "loginUser:success")
                    _loginSuccess.value = resources.getString(R.string.loginSuccess)
                    _loginSuccess.value = null
                } else {
                    Log.w(TAG, "loginUser:failure", task.exception)
                    _error.value = resources.getString(R.string.loginFailure)
                    _error.value = null
                }
            }
    }

    /**
     * Return information about currently logged in user
     */
    fun getCurrentUser(): FirebaseUser? {
        if (!isLoggedIn()) {
            return null
        } else {
            //Checked if logged in, so user is now guaranteed to be present
            return authentication.currentUser!!
        }
    }

    /**
     * Sign out through firebase signout method, which also triggers an auth state changed event
     */
    fun signOut() {
        authentication.signOut()
    }
}
