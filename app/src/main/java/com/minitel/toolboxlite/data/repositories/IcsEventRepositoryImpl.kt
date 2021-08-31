package com.minitel.toolboxlite.data.repositories

import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import com.minitel.toolboxlite.domain.repositories.IcsEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class IcsEventRepositoryImpl @Inject constructor(private val icsEventDao: IcsEventDao) :
    IcsEventRepository {
    override fun watchEventsAfterNow(): Flow<List<IcsEvent>> {
        val now = LocalDateTime.now()
        return icsEventDao.watch().map { list ->
            list.filter { it.dtstart > now || it.dtend > now }
                .map { it.asEntity() }
        }
    }
}
