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
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_files.*
import javax.inject.Inject


class FilesActivity : AppCompatActivity() {

    @Inject
    lateinit var filesDownloaderViewModelFactory: ViewModelFactory<FilesDownloaderViewModel>
    private val filesDownloaderViewModel by lazy {
        ViewModelProviders.of(this, filesDownloaderViewModelFactory)
            .get(FilesDownloaderViewModel::class.java)
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
                        val fileItem = filesDownloaderViewModel.filesAdapter.items[fileIndex.toInt()].fileItem
                        filesDownloaderViewModel.startDownload(fileItem.url, getRootDirPath(), fileItem.name, fileIndex.toInt())
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
        filesDownloaderViewModel.filesLiveData.observe(this, {
            showLoading(false)
            it?.let {
                filesDownloaderViewModel.filesAdapter.items = it.mapFileItemToUIModel()
                filesDownloaderViewModel.filesAdapter.items.forEach { item ->
                    if (filesDownloaderViewModel.isFileDownloaded(item.fileItem.id.toString())) {
                        item.downloadState = DownloadState.COMPLETED
                    } else {
                        item.downloadState = DownloadState.NORMAL
                    }
                }
                filesDownloaderViewModel.filesAdapter.notifyDataSetChanged()
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
        filesDownloaderViewModel.filesAdapter = FilesAdapter()
        rvFilesList.adapter = filesDownloaderViewModel.filesAdapter
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
        filesDownloaderViewModel.filesAdapter.tracker = tracker
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
        filesDownloaderViewModel.filesAdapter.onItemClicked.observe(this, { item ->
            val dirPath = getRootDirPath()
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
                            filesDownloaderViewModel.startDownload(
                                item.fileItem.url,
                                dirPath,
                                item.fileItem.name,
                                filesDownloaderViewModel.filesAdapter.items.indexOf(item)
                            )
                        })
                }
            }
        })
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