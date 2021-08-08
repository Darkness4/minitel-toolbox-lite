package com.minitel.toolboxlite.data.services

import com.minitel.toolboxlite.core.errors.CannotFetchIcs
import com.minitel.toolboxlite.core.errors.FailedLogin
import com.minitel.toolboxlite.core.errors.FormNotFound
import com.minitel.toolboxlite.domain.services.EmseAuthService
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Parameters
import javax.inject.Inject

private data class FormParameters(
    val lt: String,
    val action: String,
    val execution: String,
)

class EmseAuthServiceImpl @Inject constructor(
    private val client: HttpClient
) : EmseAuthService {
    companion object {
        const val ICS_BASE_URL = "https://portail.emse.fr/ics/"
    }

    /** Login CAS Emse Portal and store the cookie. */
    @Throws(FailedLogin::class)
    override suspend fun login(username: String, password: String, service: String): String {
        val formParameters = fetchForm(service)
        val response = postForm(username, password, formParameters)
        val body = response.receive<String>()
        if ("class=\"errors\"" in body || response.status.value in 400..599) {
            throw FailedLogin
        }

        return body
    }

    @Throws(FormNotFound::class)
    override suspend fun loginForIcs(username: String, password: String): String {
        return login(username, password, ICS_BASE_URL)
    }

    @Throws(CannotFetchIcs::class)
    override suspend fun findIcs(): String {
        val body = client.get<String>("https://portail.emse.fr/ics/")
        return try {
            Regex("""https(.*)\.ics""").find(body)?.value!!
        } catch (e: RuntimeException) {
            throw CannotFetchIcs
        }
    }

    @Throws(FormNotFound::class)
    private suspend fun fetchForm(service: String): FormParameters {
        val response =
            client.get<HttpResponse>("https://cas.emse.fr/login") {
                parameter("service", service)
            }
        val body = response.receive<String>()

        try {
            val lt = Regex("""name="lt" value="([^"]*)"""").find(body)?.groupValues?.get(1)!!
            val execution =
                Regex("""name="execution" value="([^"]*)"""").find(body)?.groupValues?.get(1)!!
            val action = Regex("""action="([^"]*)"""").find(body)?.groupValues?.get(1)!!
            return FormParameters(
                lt = lt,
                execution = execution,
                action = action,
            )
        } catch (e: NullPointerException) {
            throw FormNotFound
        }
    }

    private suspend fun postForm(
        username: String,
        password: String,
        formParameters: FormParameters
    ) = client.submitForm<HttpResponse>(
        url = "https://cas.emse.fr${formParameters.action}",
        formParameters = Parameters.build {
            append("username", username)
            append("password", password)
            append("lt", formParameters.lt)
            append("execution", formParameters.execution)
            append("_eventId", "submit")
        },
        encodeInQuery = true
    )
}
