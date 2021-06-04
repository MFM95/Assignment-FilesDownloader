package com.filedownloader.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.filedownloader.core.BaseViewModel
import com.filedownloader.data.source.model.FileItem
import com.filedownloader.domain.interactor.ParseJsonFileUseCase
import com.filedownloader.domain.interactor.ReadFileUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class JsonFileViewModel @Inject constructor(
    private val readFileUseCase: ReadFileUseCase,
    private val parseJsonFileUseCase: ParseJsonFileUseCase
) :
    BaseViewModel() {

    val filesLiveData by lazy { MutableLiveData<ArrayList<FileItem>>() }
    val errorLiveData by lazy { MutableLiveData<Throwable>() }

    fun readFile(fileName: String) {
        readFileUseCase.readFile(fileName)
            .flatMap {
                parseJsonFileUseCase.parseJson(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    filesLiveData.postValue(it)
                }, {
                    errorLiveData.postValue(it)
                }
            )
            .addTo(compositeDisposable)
    }
}