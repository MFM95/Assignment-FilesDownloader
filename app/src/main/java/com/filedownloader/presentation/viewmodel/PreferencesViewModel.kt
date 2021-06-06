package com.filedownloader.presentation.viewmodel

import android.content.SharedPreferences
import com.filedownloader.core.BaseViewModel
import com.filedownloader.core.di.PreferenceInfo
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(@PreferenceInfo private val sharedPreferences: SharedPreferences) :
    BaseViewModel() {

    fun saveDownloadedFileID(id: String) {
        var list = getDownloadedFilesIDs()
        if (list == null)
            list = ArrayList()
        list.add(id)
        val set = HashSet<String>()
        set.addAll(list)
        val editor = sharedPreferences.edit()
        editor.putStringSet(KEY_FILES_LIST, set)
        editor.apply()
    }

    private fun getDownloadedFilesIDs(): ArrayList<String>? {
        val set = sharedPreferences.getStringSet(KEY_FILES_LIST, null)
        return set?.let { ArrayList(set) }
    }

    fun isFileDownloaded(id: String): Boolean {
        return getDownloadedFilesIDs()?.contains(id)?: false
    }

    companion object {
        private const val KEY_FILES_LIST = "key_files_list"
    }
}