package com.filedownloader.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.filesdownloader.R
import com.filedownloader.data.source.model.FileTypeEnum
import java.io.File
import java.util.*


@SuppressLint("SdCardPath")
fun Activity.getRootDirPath(fileName: String): String {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file: File = ContextCompat.getExternalFilesDirs(
            this.applicationContext,
            null
        )[0]
        file.absolutePath
    } else {
        this.applicationContext.filesDir.absolutePath
    }
}

fun Activity.openFile(fileName: String, fileType: FileTypeEnum) {
    val intent = Intent(Intent.ACTION_VIEW)
    val rootPath = "storage/emulated/0/Android/data/com.example.filesdownloader/files/"
    val uri = Uri.parse(rootPath + fileName)
    when (fileType) {
        FileTypeEnum.PDF -> {
            intent.setDataAndType(uri, "application/pdf")
        }
        FileTypeEnum.VIDEO -> {
            intent.setDataAndType(uri, "video/*")
        }
        else -> {
            intent.setDataAndType(uri, "*/*")
        }
    }
    Log.i("openFile", uri.path.toString())
    startActivity(Intent.createChooser(intent, "Open folder"))
}


fun Activity.showConfirmationDialog(title: String, message: String, positiveBtnAction: View.OnClickListener? = null, negativeBtnAction: View.OnClickListener? = null) {
    val alertDialog: AlertDialog = this.let {
        val builder = AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog)
        builder.apply {
            setPositiveButton("YES") { dialog, _ ->
                positiveBtnAction?.onClick(null)
                dialog.dismiss()
            }
            setNegativeButton("NO") { dialog, _ ->
                negativeBtnAction?.onClick(null)
                dialog.dismiss()
            }
        }
        builder.setTitle(title)
        builder.setMessage(message)
        builder.create()
    }
    alertDialog.setOnShowListener {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.russian_violet))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.russian_violet))
    }
    alertDialog.show()
}
