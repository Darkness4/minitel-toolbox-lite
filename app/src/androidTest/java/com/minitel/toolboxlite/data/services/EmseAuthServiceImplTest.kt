package com.minitel.toolboxlite.data.services

import com.minitel.toolboxlite.core.di.DataModule
import com.minitel.toolboxlite.core.errors.FailedLogin
import com.minitel.toolboxlite.domain.services.EmseAuthService
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

internal class EmseAuthServiceImplTest {
    lateinit var client: HttpClient
    lateinit var service: EmseAuthService

    @Before
    fun setUp() {
        client = DataModule.provideKtorClient(DataModule.provideHttpClient())
        service = EmseAuthServiceImpl(client)
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
}
