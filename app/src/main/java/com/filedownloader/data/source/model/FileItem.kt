package com.filedownloader.data.source.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileItem(
    val id: Int,
    val type: FileTypeEnum,
    val url: String,
    val name: String
) : Parcelable