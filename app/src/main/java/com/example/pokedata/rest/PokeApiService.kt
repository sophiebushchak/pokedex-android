package com.example.pokedata.rest

import com.example.pokedata.rest.response.PokemonEvolutionChain
import com.example.pokedata.rest.response.PokedexCountResponse
import com.example.pokedata.rest.response.PokedexResponse
import com.example.pokedata.rest.response.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokedex")
    suspend fun getPokedexPaginated(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokedexResponse

    @GET("pokedex")
    suspend fun getPokedexWithSearch(
        @Query("name") name: String,
    ): PokedexResponse

    @GET("pokedex/count")
    suspend fun getPokedexTotalPokemon(): PokedexCountResponse

    @GET("pokemon/{pokedexNumber}")
    suspend fun getPokemonByPokedexNumber(@Path("pokedexNumber") pokedexNumber: Int): PokemonResponse

    @GET("pokemon/evolutions/{pokedexNumber}")
    suspend fun getPokemonEvolutionChain(@Path("pokedexNumber") pokedexNumber: Int): PokemonEvolutionChain
}