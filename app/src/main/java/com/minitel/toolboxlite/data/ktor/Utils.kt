package com.minitel.toolboxlite.data.ktor

import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.http.hostIsIp
import io.ktor.http.isSecure

internal fun Cookie.matches(requestUrl: Url): Boolean {
    val domain = domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
        ?: error("Domain field should have the default value")

    val path = with(path) {
        val current = path ?: error("Path field should have the default value")
        if (current.endsWith('/')) current else "$path/"
    }

    val host = requestUrl.host.toLowerCasePreservingASCIIRules()
    val requestPath = let {
        val pathInRequest = requestUrl.encodedPath
        if (pathInRequest.endsWith('/')) pathInRequest else "$pathInRequest/"
    }

    if (host != domain && (hostIsIp(host) || !host.endsWith(".$domain"))) {
        return false
    }

    if (path != "/" &&
        requestPath != path &&
        !requestPath.startsWith(path)
    ) return false

    return !(secure && !requestUrl.protocol.isSecure())
}

internal fun Cookie.fillDefaults(requestUrl: Url): Cookie {
    var result = this

    if (result.path?.startsWith("/") != true) {
        result = result.copy(path = requestUrl.encodedPath)
    }

    if (result.domain.isNullOrBlank()) {
        result = result.copy(domain = requestUrl.host)
    }

    return result
}

private fun String.toLowerCasePreservingASCIIRules(): String {
    val firstIndex = indexOfFirst {
        toLowerCasePreservingASCII(it) != it
    }

    if (firstIndex == -1) {
        return this
    }

    val original = this
    return buildString(length) {
        append(original, 0, firstIndex)

        for (index in firstIndex..original.lastIndex) {
            append(toLowerCasePreservingASCII(original[index]))
        }
    }
}

private fun toLowerCasePreservingASCII(ch: Char): Char = when (ch) {
    in 'A'..'Z' -> ch + 32
    in '\u0000'..'\u007f' -> ch
    else -> ch.lowercaseChar()
}
