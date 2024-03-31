package br.com.listennow.webclient

import br.com.listennow.service.SongService
import br.com.listennow.service.UserService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitInitializer {
    private val client by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.devpree.com.br/listennow/")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()

    val userService = retrofit.create(UserService::class.java)
    val songService = retrofit.create(SongService::class.java)
}