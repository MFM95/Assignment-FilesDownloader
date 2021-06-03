package com.filedownloader.core.di

import com.filedownloader.presentation.view.activity.FilesActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(
        modules = []
    )
    abstract fun bindEmitterUsersActivity(): FilesActivity

}