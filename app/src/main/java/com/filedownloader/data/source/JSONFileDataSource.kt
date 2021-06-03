package com.filedownloader.data.source

import android.app.Application
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.inject.Inject

class JSONFileDataSource @Inject constructor(private val application: Application) {

    fun readFile(fileName: String) {
        var reader: BufferedReader? = null
        reader = BufferedReader(
            InputStreamReader(application.assets.open(fileName), "UTF-8")
        )

        var word: String? = ""
        val stringBuilder = StringBuilder()
        while (true) {
            try {
                if (reader.readLine().also { word = it } == null) break
            } catch (e: IOException) {
                e.printStackTrace()
            }
            stringBuilder.append(word)
        }

        Log.i("JSONFileDataSource", stringBuilder.toString())
    }
}