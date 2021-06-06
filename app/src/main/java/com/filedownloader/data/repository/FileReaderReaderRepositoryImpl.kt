package com.filedownloader.data.repository

import com.filedownloader.data.source.FileReaderDataSource
import com.filedownloader.domain.repository.FileReaderRepository
import io.reactivex.Observable
import javax.inject.Inject

class FileReaderReaderRepositoryImpl @Inject constructor(private val fileReaderDataSource: FileReaderDataSource)
    : FileReaderRepository {
    override fun readFile(fileName: String): Observable<String> {
        return fileReaderDataSource.readFile(fileName)
    }

}