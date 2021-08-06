package com.minitel.toolboxlite.core.di

import com.minitel.toolboxlite.data.repositories.IcsEventRepositoryImpl
import com.minitel.toolboxlite.data.services.EmseAuthServiceImpl
import com.minitel.toolboxlite.domain.repositories.IcsEventRepository
import com.minitel.toolboxlite.domain.services.EmseAuthService
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
    fun bindIcsEventRepository(repository: IcsEventRepositoryImpl): IcsEventRepository

    @Binds
    @Singleton
    fun bindEmseAuthService(service: EmseAuthServiceImpl): EmseAuthService
}
