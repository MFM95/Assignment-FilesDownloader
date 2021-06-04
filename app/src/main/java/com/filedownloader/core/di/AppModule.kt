package com.filedownloader.core.di

import android.app.Application
import android.content.Context
import com.filedownloader.data.repository.FileReaderReaderRepositoryImpl
import com.filedownloader.domain.repository.FileReaderRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }


    @Provides
    fun provideVodRepository(fileReaderRepositoryImpl: FileReaderReaderRepositoryImpl): FileReaderRepository =
        fileReaderRepositoryImpl



    companion object {
        private const val API_BASE_URL = "https://jsonplaceholder.typicode.com"
    }
}