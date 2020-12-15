package com.example.pokedata.rest

import com.example.pokedata.rest.pokedex.PokedexResource
import com.example.pokedata.rest.pokedex.PokemonEntryResource
import com.example.pokedata.rest.pokedex.PokemonResource
import com.example.pokedata.rest.pokedex.PokemonSpeciesResource
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokedex/national")
    suspend fun getNationalPokedex(): PokedexResource

    @GET("pokemon/")
    suspend fun getAllPokemon(@Query("limit") limit: Int)

    @GET("pokemon/{id}")
    suspend fun getPokemonById(@Path("id") id: Int): PokemonResource

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpeciesByName(@Path("name") name: String): PokemonSpeciesResource

    @GET("generation/{name}")
    suspend fun getGenerationByName(@Path("name") name: String)
}