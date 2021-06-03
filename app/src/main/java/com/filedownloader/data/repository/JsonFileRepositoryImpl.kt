package com.filedownloader.data.repository

import com.filedownloader.data.source.JSONFileDataSource
import com.filedownloader.domain.repository.JsonFileRepository
import javax.inject.Inject

class JsonFileRepositoryImpl @Inject constructor(private val jsonFileDataSource: JSONFileDataSource)
    : JsonFileRepository {
    override fun readFile(fileName: String) {
        jsonFileDataSource.readFile(fileName)
    }

}