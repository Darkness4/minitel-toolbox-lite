package com.minitel.toolboxlite.domain.services

interface IcsDownloader {
    suspend fun download(path: String)
}
