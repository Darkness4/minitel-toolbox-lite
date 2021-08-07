package com.minitel.toolboxlite.core.di

import android.content.Context
import androidx.room.Room
import com.minitel.toolboxlite.BuildConfig
import com.minitel.toolboxlite.data.database.Database
import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.cookies.AcceptAllCookiesStorage
import io.ktor.client.features.cookies.HttpCookies
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideKtorClient(client: OkHttpClient) = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(false)
                followSslRedirects(false)
            }

            preconfigured = client
        }

        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return when {
            BuildConfig.DEBUG -> {
                val interceptor =
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                OkHttpClient.Builder().addInterceptor(interceptor).build()
            }
            else -> OkHttpClient()
        }
    }

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "cache.db")
            .addTypeConverter(LocalDateTimeConverters())
            .build()
    }

    @Singleton
    @Provides
    fun provideIcsEventDao(database: Database): IcsEventDao {
        return database.icsEventDao()
    }
}
