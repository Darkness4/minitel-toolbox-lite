package com.minitel.toolboxlite.data.services

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.minitel.toolboxlite.core.di.DataModule
import com.minitel.toolboxlite.data.database.Database
import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import com.minitel.toolboxlite.data.database.converters.MapConverters
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@Ignore("This test requires username and password.")
internal class IcsDownloaderImplTest {
    private lateinit var client: HttpClient
    private lateinit var eventDao: IcsEventDao
    private lateinit var downloader: IcsDownloaderImpl
    private lateinit var db: Database

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val json = DataModule.provideJson()
        val mapConverters = MapConverters.create(json)
        db = Room.inMemoryDatabaseBuilder(context, Database::class.java)
            .addTypeConverter(LocalDateTimeConverters())
            .addTypeConverter(mapConverters)
            .build()

        val cookieDao = db.cookieDao()
        eventDao = db.icsEventDao()

        val persistentCookiesStorage = DataModule.providePersistentCookiesStorage(cookieDao)
        client = DataModule.provideKtorClient(DataModule.provideHttpClient(), persistentCookiesStorage)
        val emseAuthService = EmseAuthServiceImpl(client, persistentCookiesStorage)
        runBlocking {
            emseAuthService.login(
                username = "FILL USERNAME",
                password = "FILL PASSWORD",
                service = "https://portail.emse.fr/ics/"
            )
        }
        downloader = IcsDownloaderImpl(client, eventDao)
    }

    @Test
    fun testDownload() = runBlocking {
        // Arrange
        val body = client.get<String>("https://portail.emse.fr/ics/")
        val path = Regex("""https(.*)\.ics""").find(body)?.value!!
        println(path)

        // Act
        downloader.download(path)

        // Assert
        println(eventDao.watch().first())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
