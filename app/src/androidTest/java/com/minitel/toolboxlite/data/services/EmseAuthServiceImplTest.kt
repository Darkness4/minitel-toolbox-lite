package com.minitel.toolboxlite.data.services

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.minitel.toolboxlite.core.di.DataModule
import com.minitel.toolboxlite.core.errors.FailedLogin
import com.minitel.toolboxlite.data.database.Database
import com.minitel.toolboxlite.data.database.converters.LocalDateTimeConverters
import com.minitel.toolboxlite.data.database.converters.MapConverters
import com.minitel.toolboxlite.domain.services.EmseAuthService
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
internal class EmseAuthServiceImplTest {
    private lateinit var client: HttpClient
    private lateinit var service: EmseAuthService
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
        val persistentCookiesStorage = DataModule.providePersistentCookiesStorage(cookieDao)
        client =
            DataModule.provideKtorClient(DataModule.provideHttpClient(), persistentCookiesStorage)
        service = EmseAuthServiceImpl(client, persistentCookiesStorage)
    }

    @Test
    @Ignore("This test requires username and password.")
    fun testLogin() = runBlocking {
        // Act
        val body =
            service.login(
                username = "FILL USERNAME",
                password = "FILL PASSWORD",
                service = "https://portail.emse.fr/ics/"
            )
        val lateBody = client.get<String>("https://portail.emse.fr/ics/")

        // Assert
        Assert.assertNotNull(Regex("""https(.*)\.ics""").find(body)?.value)
        Assert.assertNotNull(Regex("""https(.*)\.ics""").find(lateBody)?.value)
    }

    @Test(expected = FailedLogin::class)
    fun testFailedLogin(): Unit = runBlocking {
        // Act
        service.login("john.doe", "", "https://portail.emse.fr/ics/")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
