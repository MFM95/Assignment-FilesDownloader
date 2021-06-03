package com.filedownloader.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.filedownloader.core.BaseViewModel
import com.filedownloader.domain.interactor.ReadJsonFileUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class JsonFileViewModel @Inject constructor(private val readJsonFileUseCase: ReadJsonFileUseCase) :
    BaseViewModel() {

//    val liveData by lazy { MutableLiveData<UsersResponse>() }

    fun readFile(fileName: String) {
        readJsonFileUseCase.readFile(fileName)
    }
}