package com.filedownloader.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.filedownloader.core.BaseViewModel
import com.filedownloader.data.source.model.FileItem
import com.filedownloader.domain.interactor.ParseJsonFileUseCase
import com.filedownloader.domain.interactor.PreferencesUseCase
import com.filedownloader.domain.interactor.ReadFileUseCase
import com.filedownloader.presentation.uimodel.DownloadState
import com.filedownloader.presentation.view.adapter.FilesAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject
import kotlin.collections.ArrayList

class FilesDownloaderViewModel @Inject constructor(
    private val readFileUseCase: ReadFileUseCase,
    private val parseJsonFileUseCase: ParseJsonFileUseCase,
    private val preferencesUseCase: PreferencesUseCase
) :
    BaseViewModel() {

    val filesLiveData by lazy { MutableLiveData<ArrayList<FileItem>>() }
    val errorLiveData by lazy { MutableLiveData<Throwable>() }
    val selectedFiles by lazy { MutableLiveData<ArrayList<Long>>() }
    lateinit var filesAdapter: FilesAdapter

    fun readFile(fileName: String) {
        readFileUseCase.readFile(fileName)
            .flatMap {
                parseJsonFileUseCase.parseJson(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    filesLiveData.postValue(it)
                }, {
                    errorLiveData.postValue(it)
                }
            )
            .addTo(compositeDisposable)
    }

    fun startDownload(url: String, dirPath: String, fileName: String, position: Int) {
        if (filesAdapter.items[position].downloadState == DownloadState.COMPLETED
            || filesAdapter.items[position].downloadState == DownloadState.DOWNLOADING
            || filesAdapter.items[position].downloadState == DownloadState.PENDING
        )
            return
        updateItemState(position, DownloadState.PENDING)
        filesAdapter.items[position].downloadId =
            PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener {
                    updateItemState(position, DownloadState.DOWNLOADING)
                }
                .setOnPauseListener {
                    filesAdapter.items[position].numberOfFailures = 0
                    updateItemState(position, DownloadState.NORMAL)

                }
                .setOnCancelListener {
                    filesAdapter.items[position].numberOfFailures = 0
                    updateItemState(position, DownloadState.NORMAL)
                }
                .setOnProgressListener {
                    var progress = (it.currentBytes * 100) / it.totalBytes
                    if (it.totalBytes < 0L) {
                        progress = (it.currentBytes * 100L) / 10000000
                    }
                    updateItemState(position, DownloadState.DOWNLOADING, progress)
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        filesAdapter.items[position].numberOfFailures = 0
                        updateItemState(position, DownloadState.COMPLETED)
                        saveDownloadedFileID(filesAdapter.items[position].fileItem.id.toString())
//                        openFile(filesAdapter.items[position].fileItem.name, filesAdapter.items[position].fileItem.type)
                    }

                    override fun onError(error: com.downloader.Error?) {
                        handleErrors(url, dirPath, fileName, position)
                    }
                })
    }

    private fun updateItemState(position: Int, state: DownloadState, progress: Long? = null) {
        filesAdapter.items[position].downloadState = state
        progress?.let {
            if (progress in 0..100) {
                filesAdapter.items[position].downloadProgress = progress.toInt()
            }
        }
        filesAdapter.notifyItemChanged(position)
    }

    private fun handleErrors(url: String, dirPath: String, fileName: String, position: Int) {
        /*** Retry 3 times then display error state **/
        if (filesAdapter.items[position].numberOfFailures < 4) {
            filesAdapter.items[position].numberOfFailures++
            filesAdapter.items[position].downloadState =
                DownloadState.NORMAL
            startDownload(url, dirPath, fileName, position)
        } else {
            filesAdapter.items[position].numberOfFailures = 0
            updateItemState(position, DownloadState.FAILED)
        }
    }


    fun saveDownloadedFileID(id: String) {
        preferencesUseCase.saveDownloadedFileID(id)
    }

    fun isFileDownloaded(id: String): Boolean {
        return preferencesUseCase.isFileDownloaded(id)
    }

}