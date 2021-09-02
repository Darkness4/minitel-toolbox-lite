package com.minitel.toolboxlite.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.room.Room
import com.minitel.toolboxlite.BuildConfig
import com.minitel.toolboxlite.data.database.CookieDao
import com.minitel.toolboxlite.data.database.Database
import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import com.minitel.toolboxlite.data.database.converters.MapConverters
import com.minitel.toolboxlite.data.datastore.CalendarSettings
import com.minitel.toolboxlite.data.datastore.IcsReference
import com.minitel.toolboxlite.data.datastore.LoginSettings
import com.minitel.toolboxlite.data.datastore.calendarSettingsDataStore
import com.minitel.toolboxlite.data.datastore.icsReferenceDataStore
import com.minitel.toolboxlite.data.datastore.loginSettingsDataStore
import com.minitel.toolboxlite.data.ktor.PersistentCookiesStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.cookies.HttpCookies
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideKtorClient(client: OkHttpClient, persistentCookiesStorage: PersistentCookiesStorage) = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(false)
                followSslRedirects(false)
            }

            preconfigured = client
        }

        install(HttpCookies) {
            storage = persistentCookiesStorage
        }
    }

    @Provides
    @Singleton
    fun providePersistentCookiesStorage(cookieDao: CookieDao) = PersistentCookiesStorage(cookieDao)

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return when {
            BuildConfig.DEBUG -> {
                val interceptor =
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)
                OkHttpClient.Builder().addInterceptor(interceptor).build()
            }
            else -> OkHttpClient()
        }
    }

    @Singleton
    @Provides
    fun provideJson() = Json { isLenient = true }

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context, json: Json): Database {
        val mapConverters = MapConverters.create(json)
        return Room.databaseBuilder(context, Database::class.java, "cache.db")
            .addTypeConverter(LocalDateTimeConverters())
            .addTypeConverter(mapConverters)
            .build()
    }

    @Singleton
    @Provides
    fun provideIcsEventDao(database: Database): IcsEventDao {
        return database.icsEventDao()
    }

    @Singleton
    @Provides
    fun provideCookieDao(database: Database): CookieDao {
        return database.cookieDao()
    }

    @Singleton
    @Provides
    fun provideCalendarSettingsDataStore(@ApplicationContext context: Context): DataStore<CalendarSettings> = context.calendarSettingsDataStore

    @Singleton
    @Provides
    fun provideIcsReferenceDataStore(@ApplicationContext context: Context): DataStore<IcsReference> = context.icsReferenceDataStore

    @Singleton
    @Provides
    fun provideLoginSettingsDataStore(@ApplicationContext context: Context): DataStore<LoginSettings> = context.loginSettingsDataStore
}
