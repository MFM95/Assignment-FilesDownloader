package com.filedownloader.domain.interactor

import com.filedownloader.domain.repository.FileReaderRepository
import io.reactivex.Observable
import javax.inject.Inject

class ReadFileUseCase @Inject constructor(private val fileReaderRepository: FileReaderRepository) {
    fun readFile(fileName: String): Observable<String> {
        return fileReaderRepository.readFile(fileName)
    }
}