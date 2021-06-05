package com.filedownloader.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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


//    val direct = File(Environment.getExternalStorageDirectory().toString() + "/DirName")
//
//    if (!direct.exists()) {
//        val wallpaperDirectory = File("/sdcard/DirName/")
//        wallpaperDirectory.mkdirs()
//    }
//    val file = File("/sdcard/DirName/", fileName)
//
//    return file.absolutePath
}


fun Activity.openFile(fileName: String) {
    val intent = Intent(Intent.ACTION_VIEW)

//    val uri = Uri.parse(File(Environment.getExternalStorageDirectory().path + fileName).toString())
    val uri = Uri.fromFile(File(Environment.getExternalStorageDirectory(),fileName))
//    val uri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", File(fileName))
    Log.i("openFile", uri.path?: "XX")
    intent.setDataAndType(uri, "video/*")
    startActivity(Intent.createChooser(intent, "Open folder"))
//    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//    startActivity(intent)
}