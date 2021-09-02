package com.minitel.toolboxlite.domain.repositories

import com.minitel.toolboxlite.data.datastore.IcsReference
import kotlinx.coroutines.flow.Flow

interface IcsReferenceRepository {
    suspend fun update(username: String, path: String)
    fun watch(): Flow<IcsReference>
}
