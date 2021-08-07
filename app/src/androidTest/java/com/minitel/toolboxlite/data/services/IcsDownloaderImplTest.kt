package com.minitel.toolboxlite.data.services

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.minitel.toolboxlite.core.di.DataModule
import com.minitel.toolboxlite.data.database.Database
import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import com.minitel.toolboxlite.domain.services.EmseAuthService
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("This test requires username and password.")
internal class IcsDownloaderImplTest {
    lateinit var client: HttpClient
    lateinit var eventDao: IcsEventDao
    lateinit var emseAuthService: EmseAuthService
    lateinit var downloader: IcsDownloaderImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        client = DataModule.provideKtorClient(DataModule.provideHttpClient())
        emseAuthService = EmseAuthServiceImpl(client)
        runBlocking {
            emseAuthService.login(
                username = "FILL USERNAME",
                password = "FILL PASSWORD",
                service = "https://portail.emse.fr/ics/"
            )
        }

        eventDao = Room.inMemoryDatabaseBuilder(context, Database::class.java)
            .addTypeConverter(LocalDateTimeConverters())
            .build()
            .icsEventDao()
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
}
