package com.minitel.toolboxlite.data.datasources

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface IcsDataSource {
    companion object {
        const val BASE_URL = "https://portail.emse.fr/ics/"
    }

    @GET("{path}.ics")
    @Streaming
    suspend fun fetch(@Path("path") path: String): Response<ResponseBody>
}
