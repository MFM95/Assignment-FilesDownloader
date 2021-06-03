package com.filedownloader.data.source

import android.app.Application
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

class JSONFileDataSource @Inject constructor(private val application: Application) {

    fun readFile(fileName: String) {
        var data: String? = ""
        try {
            val stream: InputStream = application.assets.open(fileName)
            val size: Int = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            data = String(buffer)
            Log.i("JSONFileDataSource", data.toString())
        } catch (e: IOException) {
            Log.i("JSONFileDataSource", e.localizedMessage)
        }

    }
}