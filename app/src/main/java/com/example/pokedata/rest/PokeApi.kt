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

class PokeApi(context: Context) {
    private var onlineInterceptor: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val maxAge = 999999999 // read from cache for 60 seconds even if there is internet connection
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .removeHeader("Pragma")
                    .build()
        }
    }

    private val cacheSize = (25 * 1024 * 1024).toLong() // 10 MB
    private val cache = Cache(context.cacheDir, cacheSize)

    fun createApi(): PokeApiService {
        val okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(onlineInterceptor)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .cache(cache)
                .build()

        val pokeApi = Retrofit.Builder()
                .baseUrl(PokeApiConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return pokeApi.create(PokeApiService::class.java)
    }
}
