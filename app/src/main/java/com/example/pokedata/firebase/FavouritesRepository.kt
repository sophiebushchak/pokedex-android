package com.example.pokedata.firebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.lang.Exception

class FavouritesRepository {
    private val TAG: String = "FirestoreFavourites"
    private val firestore = Firebase.firestore
    private val authentication = Authentication()

    private val _favouriteStatus = MutableLiveData<Pair<String, Boolean>?>()
    val favouriteStatus: LiveData<Pair<String, Boolean>?> get() = _favouriteStatus

    private val _favouriteSuccess = MutableLiveData<Pair<String, Boolean>?>()
    val favourited: LiveData<Pair<String, Boolean>?> get() = _favouriteSuccess

    /**
     * Access firestore and get the favourite status of a particular Pokemon by name.
     */
    suspend fun getFavouriteStatus(pokemonName: String) {
        try {
            val user = authentication.getCurrentUser() ?: throw Exception("Not logged in.")
            val userCollection = firestore.collection(user.uid)
            val favouriteStatus = userCollection.document(pokemonName)
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
                                        _favouriteStatus.value = null
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
                        throw FavouritesRepositoryException("Something went wrong.", exception)
                    }
            }
        } catch (timedOutException: TimeoutCancellationException) {
            throw FavouritesRepositoryException(
                "Could not connect to the server. Please check your internet connection.",
                timedOutException
            )
        } catch (error: Throwable) {
            println(error)
            throw FavouritesRepositoryException(
                "Could not get favourite status for Pokemon $pokemonName",
                error
            )
        }
    }

    /**
     * Sets the favourite status of a particular Pokemon to true or false.
     */
    suspend fun setFavouriteStatus(pokemonName: String, isFavourite: Boolean) {
        try {
            val user = authentication.getCurrentUser() ?: throw Exception("Not logged in.")
            val userCollection = firestore.collection(user.uid)
            val favouriteStatus = userCollection.document(pokemonName)
            withTimeout(5_000) {
                favouriteStatus.set(
                    mutableMapOf(Pair("isFavourite", isFavourite))
                ).addOnSuccessListener { _ ->
                    _favouriteSuccess.value = Pair(pokemonName, isFavourite)
                    _favouriteSuccess.value = null
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to set favourite status with exception", exception)
                    throw FavouritesRepositoryException("Something went wrong.", exception)
                }
            }
        } catch (timedOutException: TimeoutCancellationException) {
            throw FavouritesRepositoryException(
                "Could not connect to the server. Please check your internet connection.",
                timedOutException
            )
        } catch (error: Throwable) {
            println(error)
            throw FavouritesRepositoryException(
                "Could not set favourite status for Pokemon $pokemonName.",
                error
            )
        }
    }

    /**
     * Retrieves a list of pairs of Pokemon names and a boolean which indicates whether a Pokemon
     * has been favourited.
     */
    suspend fun getListOfAllFavouritesByName(): List<Pair<String, Boolean>> {
        return try {
            val user = authentication.getCurrentUser() ?: throw Exception("Not logged in.")
            val userCollection = firestore.collection(user.uid)
            val result = withTimeout(5_000) {
                userCollection.get().await()
            }
            result.map { documentSnapshot ->
                Pair(documentSnapshot.id, documentSnapshot.data["isFavourite"])
            } as List<Pair<String, Boolean>>
        } catch (timedOutException: TimeoutCancellationException) {
            throw FavouritesRepositoryException(
                "Could not connect to the server. Please check your internet connection.",
                timedOutException
            )
        } catch (error: Throwable) {
            println(error)
            throw FavouritesRepositoryException("Could not get all favourites.", error)
        }
    }


    inner class FavouritesRepositoryException(message: String, cause: Throwable) :
        Throwable(message, cause)
}



