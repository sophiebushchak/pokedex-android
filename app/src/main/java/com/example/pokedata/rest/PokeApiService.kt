package com.example.pokedata.rest

import com.example.pokedata.rest.pokedex.Pokedex
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PokeApiService {
    @GET("pokedex/national")
    suspend fun getNationalPokedex(): Pokedex

    @GET("pokemon/")
    suspend fun getAllPokemon(@Query("limit") limit: Int)

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int)
}