package com.emill.workplacetracking

import com.emill.workplacetracking.Config.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/*In order to ensure that we have a single instance of Retrofit and ApiService throughout the application,
we can implement the singleton pattern.*/

// Creating Retrofit and ApiService Singletons:

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            val logger = HttpLoggingInterceptor()
            //to be able to read the response body
            logger.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .protocols(listOf(Protocol.HTTP_1_1))
                .addInterceptor(logger)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(MyAPI::class.java)
        }

    }

}