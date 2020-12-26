package com.example.pokedata.rest

import com.example.pokedata.models.PokemonBasic
import com.example.pokedata.rest.response.PokedexCountResponse
import com.example.pokedata.rest.response.PokedexResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokedex")
    suspend fun getPokedexPaginated(@Query("offset") offset: Int, @Query("limit") limit: Int): PokedexResponse

    @GET("pokedex/count")
    suspend fun getPokedexTotalPokemon(): PokedexCountResponse
}