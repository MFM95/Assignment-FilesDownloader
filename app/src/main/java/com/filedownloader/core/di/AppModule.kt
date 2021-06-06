package com.filedownloader.core.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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


    @Provides
    @PreferenceInfo
    fun providePreferenceName(): String {
        return PREF_NAME
    }

    @Provides
    @Singleton
    @PreferenceInfo
    fun providePreferenceObj(@PreferenceInfo spName: String, application: Application): SharedPreferences {
        return application.getSharedPreferences(spName, Context.MODE_PRIVATE)
    }


    companion object {
        private const val PREF_NAME = "FileDownloader_Pref"
    }
}