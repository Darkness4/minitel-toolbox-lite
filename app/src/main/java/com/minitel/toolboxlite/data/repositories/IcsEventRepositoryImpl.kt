package com.minitel.toolboxlite.data.repositories

import com.minitel.toolboxlite.data.database.IcsEventDao
import com.minitel.toolboxlite.domain.entities.calendar.IcsEvent
import com.minitel.toolboxlite.domain.repositories.IcsEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IcsEventRepositoryImpl @Inject constructor(private val icsEventDao: IcsEventDao) :
    IcsEventRepository {
    override fun watchEvents(): Flow<List<IcsEvent>> =
        icsEventDao.watch().map { it.map { m -> m.asEntity() } }
}
