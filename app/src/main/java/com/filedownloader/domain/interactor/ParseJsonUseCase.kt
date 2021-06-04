package com.filedownloader.domain.interactor

import com.filedownloader.data.source.model.FileItem
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject

class ParseJsonFileUseCase @Inject constructor() {
    fun parseJson(jsonText: String): Observable<ArrayList<FileItem>> {
        return Observable.just(Gson().fromJson(jsonText, ArrayList<FileItem>()::class.java))
    }
}