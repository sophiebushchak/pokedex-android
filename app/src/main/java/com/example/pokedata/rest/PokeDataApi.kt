package com.example.pokedata.rest

import android.content.Context
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Sets up a Retrofit instance connected to the PokeData back end.
 */
class PokeDataApi(context: Context) {
    //Set up an interceptor that will be used so that requests can be cached.
    private var cacheInterceptor: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val maxAge =
                999999999 // read from cache indefinitely, because the data should almost never change
            return response.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .removeHeader("Pragma")
                .build()
        }
    }

    private val cacheSize = (40 * 1024 * 1024).toLong() // 40 MB max cache size
    private val cache = Cache(context.cacheDir, cacheSize)

    //Build the OkHttpClient and Retrofit client
    fun createApi(): PokeDataService {
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(cacheInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .cache(cache)
            .build()

        val pokeApi = Retrofit.Builder()
            .baseUrl(PokeDataApiConfig.HOST + PokeDataApiConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return pokeApi.create(PokeDataService::class.java)
    }
}
