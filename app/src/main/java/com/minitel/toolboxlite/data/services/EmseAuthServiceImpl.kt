package com.minitel.toolboxlite.data.services

import com.minitel.toolboxlite.core.errors.CannotFetchIcs
import com.minitel.toolboxlite.core.errors.FailedLogin
import com.minitel.toolboxlite.core.errors.FormNotFound
import com.minitel.toolboxlite.data.ktor.PersistentCookiesStorage
import com.minitel.toolboxlite.domain.services.EmseAuthService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.formUrlEncode
import io.ktor.http.parametersOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

private data class FormParameters(
    val execution: String,
)

class EmseAuthServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val persistentCookiesStorage: PersistentCookiesStorage
) : EmseAuthService {
    companion object {
        const val ICS_BASE_URL = "https://portail.emse.fr/ics/"
        const val CAS_URL = "https://cas.emse.fr/"
    }

    /** Login CAS Emse Portal and store the cookie. */
    @Throws(FailedLogin::class)
    override suspend fun login(username: String, password: String, service: String): String =
        withContext(
            Dispatchers.IO
        ) {
            persistentCookiesStorage.clear(Url(ICS_BASE_URL))
            persistentCookiesStorage.clear(Url(CAS_URL))
            val formParameters = fetchForm(service)
            val response = postForm(username, password, service, formParameters)
            val body: String = response.body()
            if ("id=\"loginErrorsPanel\"" in body || response.status.value in 400..599) {
                throw FailedLogin
            }

            return@withContext body
        }

    @Throws(FormNotFound::class)
    override suspend fun loginForIcs(username: String, password: String): String = withContext(
        Dispatchers.IO
    ) {
        return@withContext login(username, password, ICS_BASE_URL)
    }

    @Throws(CannotFetchIcs::class)
    override suspend fun findIcs(): String = withContext(
        Dispatchers.IO
    ) {
        val body: String = client.get(ICS_BASE_URL).body()
        return@withContext try {
            Regex("""https(.*)\.ics""").find(body)?.value!!
        } catch (e: RuntimeException) {
            throw CannotFetchIcs
        }
    }

    override fun isSignedIn(): Flow<Boolean> =
        persistentCookiesStorage.getFlow(Url(ICS_BASE_URL)).map {
            it.isNotEmpty()
        }

    @Throws(FormNotFound::class)
    private suspend fun fetchForm(service: String): FormParameters = withContext(
        Dispatchers.IO
    ) {
        val body: String =
            client.get("$CAS_URL/login") {
                parameter("service", service)
            }.body()

        try {
            val execution =
                Regex("""name="execution" value="([^"]*)"""").find(body)?.groupValues?.get(1)!!
            return@withContext FormParameters(
                execution = execution,
            )
        } catch (e: NullPointerException) {
            throw FormNotFound
        }
    }

    private suspend fun postForm(
        username: String,
        password: String,
        service: String,
        formParameters: FormParameters
    ): HttpResponse = withContext(
        Dispatchers.IO
    ) {
        return@withContext client.submitForm(
            url = "$CAS_URL/login?" + parametersOf("service", service).formUrlEncode(),
            formParameters = Parameters.build {
                append("username", username)
                append("password", password)
                append("execution", formParameters.execution)
                append("geolocation", "")
                append("_eventId", "submit")
            },
            encodeInQuery = true
        )
    }
}
