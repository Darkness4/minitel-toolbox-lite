package com.minitel.toolboxlite.data.services

import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.models.IcsEventModel
import com.minitel.toolboxlite.domain.entities.calendar.IcsCalendar
import com.minitel.toolboxlite.domain.services.IcsDownloader
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject

class IcsDownloaderImpl @Inject constructor(
    private val client: HttpClient,
    private val eventDao: IcsEventDao
) : IcsDownloader {
    override suspend fun download(path: String) {
        var calendar: IcsCalendar? = null
        client.get<HttpStatement>(path).execute { response ->
            val channel: ByteReadChannel = response.receive()

            val flow = flow {
                while (!channel.isClosedForRead) {
                    channel.readUTF8Line()?.let {
                        emit(it)
                    }
                }
            }

            withContext(Dispatchers.Default) {
                calendar = IcsCalendar.Builder().fromLineFlow(flow).build()
            }
        }

        eventDao.clear()
        calendar?.events?.let { list ->
            val now = LocalDateTime.now()
            eventDao.insert(
                list.filter { it.dtstart > now || it.dtend > now }
                    .map { e -> Timber.d("Fetched $e"); IcsEventModel.fromEntity(e) }
            )
        }
    }
}
