package com.minitel.toolboxlite.domain.services

interface IcsDownloader {
    /** Download .ics and fill the cache */
    suspend fun download(path: String)
}
