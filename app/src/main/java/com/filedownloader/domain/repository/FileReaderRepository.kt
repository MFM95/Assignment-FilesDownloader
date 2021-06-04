package com.filedownloader.domain.repository

import io.reactivex.Observable
import io.reactivex.Single

interface FileReaderRepository  {
    fun readFile(fileName: String): Observable<String>
}