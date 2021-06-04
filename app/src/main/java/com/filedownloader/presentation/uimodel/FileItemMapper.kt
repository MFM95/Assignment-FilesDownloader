package com.filedownloader.presentation.uimodel

import com.filedownloader.data.source.model.FileItem

fun ArrayList<FileItem>.mapFileItemToUIModel(): ArrayList<FileItemUIModel> {
    val result = ArrayList<FileItemUIModel>()
    this.forEach {
        result.add(FileItemUIModel(it))
    }
    return result
}