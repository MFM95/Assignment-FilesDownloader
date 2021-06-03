package com.filedownloader.core

import android.app.Application
import com.filedownloader.core.di.AppComponent
import com.filedownloader.core.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class FileDownloaderApplication: Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        initDaggerAppComponent()
    }

    fun initDaggerAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        appComponent?.inject(this)
    }




}