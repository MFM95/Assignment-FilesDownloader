package com.filedownloader.data.source

import android.app.Application
import android.util.Log
import com.filedownloader.data.source.model.FileItem
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class FileReaderDataSource @Inject constructor(private val application: Application) {

    fun readFile(fileName: String): Observable<String> {
        var data: String? = ""
        return try {
            val stream: InputStream = application.assets.open(fileName)
            val size: Int = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            data = String(buffer)
            Observable.just(data)
        } catch (e: IOException) {
            Observable.error(e)
        }

    }
}