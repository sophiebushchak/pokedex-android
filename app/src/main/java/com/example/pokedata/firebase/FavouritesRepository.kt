package com.example.pokedata.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class FavouritesRepository {
    private val TAG: String = "FirestoreFavourites"
    private val firestore = Firebase.firestore
    private val authentication = Authentication()
    private lateinit var user: FirebaseUser
    private lateinit var favouritesDocument: DocumentReference

    private val _favouriteStatus = MutableLiveData<Pair<String, Boolean>?>()
    val favouriteStatus: LiveData<Pair<String, Boolean>?> get() = _favouriteStatus

    private val _favouriteSuccess = MutableLiveData<Pair<String, Boolean>?>()
    val favourited: LiveData<Pair<String, Boolean>?> get() = _favouriteSuccess

    private val _allFavourites = MutableLiveData<List<Pair<String, Boolean>>>()
    val allFavourites: MutableLiveData<List<Pair<String, Boolean>>> get() = _allFavourites

    init {
        this.user = authentication.getCurrentUser()
    }

    suspend fun getFavouriteStatus(pokemonName: String) {
        try {
            val favouriteStatus = firestore.collection(user.uid).document(pokemonName)
            withTimeout(5_000) {
                favouriteStatus
                    .get()
                    .addOnSuccessListener { result ->
                        if (result != null) {
                            Log.d(TAG, "DocumentSnapshot data: ${result.data}")
                            result.data?.let { dataMap ->
                                val map = dataMap as MutableMap<String, Boolean>
                                if (map.contains("isFavourite")) {
                                    dataMap["isFavourite"]?.let {
                                        _favouriteStatus.value = Pair(pokemonName, it)
                                    }
                                }
                            } ?: run {
                                favouriteStatus.set(mutableMapOf(Pair("isFavourite", false)))
                                _favouriteStatus.value = Pair(pokemonName, false)
                                _favouriteStatus.value = null
                            }
                        } else {
                            Log.d(TAG, "No such document.")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to get document with exception", exception)
                    }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    suspend fun setFavouriteStatus(pokemonName: String, isFavourite: Boolean) {
        try {
            val favouriteStatus = firestore.collection(user.uid).document(pokemonName)
            withTimeout(5_000) {
                favouriteStatus.set(
                    mutableMapOf(Pair("isFavourite", isFavourite))
                ).addOnSuccessListener { result ->
                    _favouriteSuccess.value = Pair(pokemonName, isFavourite)
                    _favouriteSuccess.value = null
                }
            }
        } catch (e: Throwable) {
            println(e)
        }
    }

    suspend fun getAllFavourites(): List<Pair<String, Boolean>> {
        return try {
            val favouriteCollection = firestore.collection(user.uid)
            val result = withTimeout(5_000) {
                favouriteCollection.get().await()
            }
            result.map { documentSnapshot ->
                Pair(documentSnapshot.id, documentSnapshot.data["isFavourite"])
            } as List<Pair<String, Boolean>>
        } catch (e: Error) {
            println(e)
            mutableListOf()
        }
    }
}



