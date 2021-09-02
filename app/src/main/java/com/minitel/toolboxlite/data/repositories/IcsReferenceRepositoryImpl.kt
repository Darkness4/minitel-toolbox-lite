package com.minitel.toolboxlite.data.repositories

import androidx.datastore.core.DataStore
import com.minitel.toolboxlite.data.datastore.IcsReference
import com.minitel.toolboxlite.domain.repositories.IcsReferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IcsReferenceRepositoryImpl@Inject constructor(
    private val dataStore: DataStore<IcsReference>,
) : IcsReferenceRepository {
    override suspend fun update(username: String, path: String) {
        dataStore.updateData { IcsReference.getDefaultInstance() }
        dataStore.updateData {
            IcsReference.newBuilder()
                .setUsername(username)
                .setPath(path)
                .build()
        }
    }

    override fun watch(): Flow<IcsReference> = dataStore.data
}
