package br.com.listennow.hilt

import android.content.Context
import br.com.listennow.BuildConfig
import br.com.listennow.database.AppDatabase
import br.com.listennow.database.dao.PlaylistDao
import br.com.listennow.database.dao.SongDao
import br.com.listennow.database.dao.UserDao
import br.com.listennow.repository.PlaylistRepository
import br.com.listennow.repository.SongRepository
import br.com.listennow.repository.UserRepository
import br.com.listennow.service.AppVersionService
import br.com.listennow.service.PlaylistService
import br.com.listennow.service.SongService
import br.com.listennow.service.UserService
import br.com.listennow.utils.MediaStoreUtil
import br.com.listennow.webclient.appversion.service.AppVersionWebClient
import br.com.listennow.webclient.client.service.UserWebClient
import br.com.listennow.webclient.playlist.PlaylistWebClient
import br.com.listennow.webclient.song.service.SongWebClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
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
        songWebClient: SongWebClient,
        mediaStore: MediaStoreUtil
    ): SongRepository {
        return SongRepository(
            songDao,
            songWebClient,
            mediaStore
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
            .addNetworkInterceptor(Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .header("Api-Key", BuildConfig.API_KEY)
                    .build()

                chain.proceed(request)
            })
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://home.devpree.com.br/listennow/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Provides
    fun provideUserDao(
        @ApplicationContext context: Context
    ): UserDao {
        return AppDatabase.getInstance(context).userDao()
    }

    @Provides
    fun providePlaylistDao(
        @ApplicationContext context: Context
    ): PlaylistDao {
        return AppDatabase.getInstance(context).playlistDao()
    }

    @Provides
    fun providePlaylistWebClient(
        retrofit: Retrofit
    ): PlaylistWebClient {
        return PlaylistWebClient(
            retrofit.create(PlaylistService::class.java)
        )
    }

    @Provides
    fun providePlaylistRepository(
        playlistDao: PlaylistDao,
        playlistWebClient: PlaylistWebClient
    ): PlaylistRepository {
        return PlaylistRepository(
            playlistDao,
            playlistWebClient
        )
    }

    @Provides
    fun provideUserWebClient(
        retrofit: Retrofit
    ): UserWebClient {
        return UserWebClient(
            retrofit.create(UserService::class.java)
        )
    }

    @Provides
    fun provideUserRepository(
        userDao: UserDao,
        userWebClient: UserWebClient
    ): UserRepository  {
        return UserRepository(userDao, userWebClient)
    }

    @Provides
    fun provideAppVersionWebClient(
        retrofit: Retrofit
    ): AppVersionWebClient {
        return AppVersionWebClient(
            retrofit.create(AppVersionService::class.java)
        )
    }

    @Provides
    fun provideMediaStore(
        @ApplicationContext context: Context
    ): MediaStoreUtil {
        return MediaStoreUtil(context.contentResolver)
    }
}