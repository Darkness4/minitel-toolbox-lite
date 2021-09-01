package com.minitel.toolboxlite.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object IcsReferenceSerializer : Serializer<IcsReference> {
    override val defaultValue: IcsReference = IcsReference.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): IcsReference {
        return try {
            withContext(Dispatchers.IO) {
                IcsReference.parseFrom(input)
            }
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: IcsReference, output: OutputStream) {
        withContext(Dispatchers.IO) {
            t.writeTo(output)
        }
    }
}

val Context.icsReferenceDataStore: DataStore<IcsReference> by dataStore(
    fileName = "icsreference.pb",
    serializer = IcsReferenceSerializer
)

suspend fun DataStore<IcsReference>.update(username: String, path: String) {
    updateData { IcsReference.getDefaultInstance() }
    updateData {
        IcsReference.newBuilder()
            .setUsername(username)
            .setPath(path)
            .build()
    }
}
