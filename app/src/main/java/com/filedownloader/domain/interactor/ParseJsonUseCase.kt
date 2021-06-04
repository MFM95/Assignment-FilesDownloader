package com.filedownloader.domain.interactor

import com.filedownloader.data.source.model.FileItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import javax.inject.Inject

class ParseJsonFileUseCase @Inject constructor() {
    fun parseJson(jsonText: String): Observable<ArrayList<FileItem>> {
        val type = object : TypeToken<ArrayList<FileItem>>() {
        }.type
        return Observable.just(
            Gson().fromJson<ArrayList<FileItem>>(
                jsonText,
                type
            )
        )
    }
}