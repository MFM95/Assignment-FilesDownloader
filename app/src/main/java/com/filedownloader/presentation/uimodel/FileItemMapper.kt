package com.filedownloader.presentation.uimodel

import com.filedownloader.data.source.model.FileItem

fun ArrayList<FileItem>.mapFileItemToUIModel(): ArrayList<FileItemUIModel> {
    val result = ArrayList<FileItemUIModel>()
    this.forEach {
        val itemUIModel = FileItemUIModel(it)
        result.add(itemUIModel)
    }
    return result
}