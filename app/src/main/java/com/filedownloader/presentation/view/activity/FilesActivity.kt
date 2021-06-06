package com.filedownloader.presentation.view.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.filesdownloader.R
import com.filedownloader.core.ViewModelFactory
import com.filedownloader.core.getRootDirPath
import com.filedownloader.core.openFile
import com.filedownloader.core.showConfirmationDialog
import com.filedownloader.presentation.uimodel.DownloadState
import com.filedownloader.presentation.uimodel.mapFileItemToUIModel
import com.filedownloader.presentation.view.adapter.FilesAdapter
import com.filedownloader.presentation.view.utils.MyItemKeyProvider
import com.filedownloader.presentation.viewmodel.FilesDownloaderViewModel
import com.filedownloader.presentation.viewmodel.PreferencesViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_files.*
import javax.inject.Inject


class FilesActivity : AppCompatActivity() {

    private lateinit var filesAdapter: FilesAdapter

    @Inject
    lateinit var filesDownloaderViewModelFactory: ViewModelFactory<FilesDownloaderViewModel>
    private val filesDownloaderViewModel by lazy {
        ViewModelProviders.of(this, filesDownloaderViewModelFactory)
            .get(FilesDownloaderViewModel::class.java)
    }

    @Inject
    lateinit var preferencesViewModelFactory: ViewModelFactory<PreferencesViewModel>
    private val preferencesViewModel by lazy {
        ViewModelProviders.of(this, preferencesViewModelFactory)
            .get(PreferencesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_files)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_menu, menu)
        val downloadAction = menu?.findItem(R.id.action_download)
        downloadAction?.isVisible = !filesDownloaderViewModel.selectedFiles.value.isNullOrEmpty()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_download -> {
                filesDownloaderViewModel.selectedFiles.value?.let { selectedFiles ->
                    /*** to download files in order  **/
                    selectedFiles.sort()
                    for (fileIndex in selectedFiles) {
                        val fileItem = filesAdapter.items[fileIndex.toInt()].fileItem
                        startDownload(fileItem.url, getRootDirPath(fileItem.name), fileItem.name, fileIndex.toInt())
                    }
                }
            }
        }
        return true
    }

    private fun init () {
        setUpRecyclerView()
        getFiles()
        observeOnFiles()
        observeOnFilesSelection()
    }

    private fun getFiles() {
        showLoading(true)
        filesDownloaderViewModel.readFile(FILE_NAME)
    }

    private fun observeOnFiles() {
        filesDownloaderViewModel.filesLiveData.observe(this, Observer {
            showLoading(false)
            it?.let {
                filesAdapter.items = it.mapFileItemToUIModel()
                filesAdapter.items.forEach { item ->
                    if (preferencesViewModel.isFileDownloaded(item.fileItem.id.toString())) {
                        item.downloadState = DownloadState.COMPLETED
                    } else {
                        item.downloadState = DownloadState.NORMAL
                    }
                }
                filesAdapter.notifyDataSetChanged()
            }
        })

        filesDownloaderViewModel.errorLiveData.observe(this, Observer {
            showLoading(false)
        })
    }

    private fun showLoading(show: Boolean) {
        if (show) {
            pbFilesLoading.visibility = View.VISIBLE
            rvFilesList.visibility = View.GONE
        }
        else {
            pbFilesLoading.visibility = View.GONE
            rvFilesList.visibility = View.VISIBLE
        }
    }

    private fun setUpRecyclerView() {
        filesAdapter = FilesAdapter()
        rvFilesList.adapter = filesAdapter
        rvFilesList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        setSelectionTracker()
        setItemClickListener()
        (rvFilesList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun setSelectionTracker() {
        val tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            rvFilesList,
            MyItemKeyProvider(rvFilesList),
            FilesAdapter.MyItemDetailsLookup(rvFilesList),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        filesAdapter.tracker = tracker
        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    filesDownloaderViewModel.selectedFiles.value = ArrayList()
                    filesDownloaderViewModel.selectedFiles.value?.addAll(tracker.selection)
                }
            }
        )
    }

    private fun setItemClickListener() {
        filesAdapter.onItemClicked.observe(this, { item ->
            val dirPath = getRootDirPath(item.fileItem.name)
            Log.i("onItemClicked", dirPath)
            when (item.downloadState) {
                DownloadState.COMPLETED -> {
                    openFile(item.fileItem.name, item.fileItem.type)
                }
                DownloadState.DOWNLOADING, DownloadState.PENDING -> {
                }
                else -> {
                    showConfirmationDialog(getString(R.string.confirmation_title),
                        getString(R.string.confirmation_message),
                        {
                            startDownload(
                                item.fileItem.url,
                                dirPath,
                                item.fileItem.name,
                                filesAdapter.items.indexOf(item)
                            )
                        })
                }
            }
        })
    }

    private fun startDownload(url: String, dirPath: String, fileName: String, position: Int) {
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
                        preferencesViewModel.saveDownloadedFileID(filesAdapter.items[position].fileItem.id.toString())
                        openFile(filesAdapter.items[position].fileItem.name, filesAdapter.items[position].fileItem.type)
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

        if(filesAdapter.items[position].numberOfFailures < 4) {
            filesAdapter.items[position].numberOfFailures++
            filesAdapter.items[position].downloadState = DownloadState.NORMAL
            startDownload(url, dirPath, fileName, position)
        } else {
            filesAdapter.items[position].numberOfFailures = 0
            updateItemState(position, DownloadState.FAILED)
        }
    }

    private fun observeOnFilesSelection() {
        filesDownloaderViewModel.selectedFiles.observe(this, Observer {
            it?.let {
                invalidateOptionsMenu()
            }
        })
    }
    companion object {
        private const val FILE_NAME = "getListOfFilesResponse.json"
    }

}