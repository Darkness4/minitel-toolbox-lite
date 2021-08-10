package com.minitel.toolboxlite.core.di

import com.minitel.toolboxlite.data.repositories.IcsEventRepositoryImpl
import com.minitel.toolboxlite.data.services.EmseAuthServiceImpl
import com.minitel.toolboxlite.data.services.IcsDownloaderImpl
import com.minitel.toolboxlite.domain.repositories.IcsEventRepository
import com.minitel.toolboxlite.domain.services.EmseAuthService
import com.minitel.toolboxlite.domain.services.IcsDownloader
import com.minitel.toolboxlite.domain.services.IcsEventScheduler
import com.minitel.toolboxlite.presentation.services.IcsEventSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {
    @Binds
    @Singleton
    fun bindIcsEventRepository(impl: IcsEventRepositoryImpl): IcsEventRepository

    @Binds
    @Singleton
    fun bindEmseAuthService(impl: EmseAuthServiceImpl): EmseAuthService

    @Binds
    @Singleton
    fun bindIcsDownloader(impl: IcsDownloaderImpl): IcsDownloader

    @Binds
    @Singleton
    fun bindIcsEventScheduler(impl: IcsEventSchedulerImpl): IcsEventScheduler
}
