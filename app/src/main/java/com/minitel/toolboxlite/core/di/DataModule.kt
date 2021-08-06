package com.minitel.toolboxlite.core.di

import android.content.Context
import androidx.room.Room
import com.minitel.toolboxlite.BuildConfig
import com.minitel.toolboxlite.data.database.Database
import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import com.minitel.toolboxlite.data.datasources.IcsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideIcsDataSource(client: OkHttpClient): IcsDataSource {
        return Retrofit.Builder()
            .baseUrl(IcsDataSource.BASE_URL)
            .client(client)
            .build()
            .create(IcsDataSource::class.java)
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
    fun provideRoomDatabase(@ApplicationContext context: Context, json: Json): Database {
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
