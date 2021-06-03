package com.filedownloader.domain.interactor

import com.filedownloader.domain.repository.JsonFileRepository
import io.reactivex.Single
import javax.inject.Inject

class ReadJsonFileUseCase @Inject constructor(private val jsonFileRepository: JsonFileRepository) {
    fun readFile(fileName: String){
        return jsonFileRepository.readFile(fileName)
    }
}