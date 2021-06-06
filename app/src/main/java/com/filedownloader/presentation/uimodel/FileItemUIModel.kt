package com.filedownloader.presentation.uimodel

import com.filedownloader.data.source.model.FileItem

data class FileItemUIModel(
    val fileItem: FileItem
) {
    var downloadState: DownloadState? = null
    var downloadProgress: Int? = 0
    var downloadId: Int? = null
}