package com.minitel.toolboxlite.data.services

import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.data.models.IcsEventModel
import com.minitel.toolboxlite.domain.entities.calendar.IcsCalendar
import com.minitel.toolboxlite.domain.services.IcsDownloader
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.prepareGet
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
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
        client.prepareGet(path).execute { response ->
            val channel: ByteReadChannel = response.body()

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
