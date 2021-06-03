package com.filedownloader.domain.repository

import io.reactivex.Single
import java.util.*

interface JsonFileRepository  {
    fun readFile(fileName: String)
}