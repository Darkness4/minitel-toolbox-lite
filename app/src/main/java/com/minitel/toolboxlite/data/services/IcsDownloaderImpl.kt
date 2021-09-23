package com.minitel.toolboxlite.data.services

import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.models.IcsEventModel
import com.minitel.toolboxlite.domain.entities.calendar.IcsCalendar
import com.minitel.toolboxlite.domain.services.IcsDownloader
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class IcsDownloaderImpl @Inject constructor(
    private val client: HttpClient,
    private val eventDao: IcsEventDao
) : IcsDownloader {
    override suspend fun download(path: String): Unit = withContext(
        Dispatchers.IO
    ) {
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
                calendar = IcsCalendar.fromLineFlow(flow)
            }
        }

        eventDao.clear()
        calendar?.events?.let { list ->
            val now = LocalDateTime.now()
            eventDao.insert(
                list.filter { it.dtstart > now || it.dtend > now }
                    .map { e -> IcsEventModel.fromEntity(e) }
            )
        }
    }
}
