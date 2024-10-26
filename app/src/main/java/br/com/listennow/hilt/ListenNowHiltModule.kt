package br.com.listennow.hilt

import android.content.Context
import br.com.listennow.database.AppDatabase
import br.com.listennow.database.dao.SongDao
import br.com.listennow.repository.SongRepository
import br.com.listennow.service.SongService
import br.com.listennow.webclient.song.service.SongWebClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ListenNowHiltModule {
    @Provides
    fun provideSongRepository(
        songDao: SongDao,
        songWebClient: SongWebClient
    ): SongRepository {
        return SongRepository(
            songDao,
            songWebClient
        )
    }

    @Provides
    fun provideSongDao(
        @ApplicationContext context: Context
    ): SongDao {
        return AppDatabase.getInstance(context).songDao()
    }

    @Provides
    fun provideSongWebClient(
        retrofit: Retrofit
    ): SongWebClient {
        return SongWebClient(
            retrofit.create(SongService::class.java)
        )
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.devpree.com.br/listennow/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }
}