package com.filedownloader.presentation.view.activity

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
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
import com.filedownloader.presentation.viewmodel.JsonFileViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_files.*
import javax.inject.Inject


class FilesActivity : AppCompatActivity() {

    private lateinit var filesAdapter: FilesAdapter

    @Inject
    lateinit var jsonFileViewModelFactory: ViewModelFactory<JsonFileViewModel>
    private val jsonFileViewModel by lazy {
        ViewModelProviders.of(this, jsonFileViewModelFactory)
            .get(JsonFileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_files)
        init()
    }

    private fun init () {
        setUpRecyclerView()
        getFiles()
        observeOnFiles()
    }

    private fun getFiles() {
        showLoading(true)
        jsonFileViewModel.readFile(FILE_NAME)
    }

    private fun observeOnFiles() {
        jsonFileViewModel.filesLiveData.observe(this, Observer {
            showLoading(false)
            it?.let {
                filesAdapter.items = it.mapFileItemToUIModel()
                filesAdapter.notifyDataSetChanged()
            }
        })

        jsonFileViewModel.errorLiveData.observe(this, Observer {
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
                    Log.i("onSelectionChanged", tracker.selection.size().toString())
                }
            }
        )
    }

    private fun setItemClickListener() {
        filesAdapter.onItemClicked.observe(this, { item ->
            Log.i("onItemClicked", filesAdapter.items.indexOf(item).toString())
//            val dirPath = filesDir.absolutePath + File.separator + "downloads"
            val dirPath = getRootDirPath(item.fileItem.name)
//            val dirPath = android.os.Environment.getExternalStorageDirectory().absolutePath +
//                    File.separator+"downloads"
            Log.i("onItemClicked", dirPath)

            showConfirmationDialog("Confirmation",
                "Downloading will be starting now, Continue?",
                {
                    startDownload(
                        item.fileItem.url,
                        dirPath,
                        item.fileItem.name,
                        filesAdapter.items.indexOf(item)
                    )
                })
        })
    }

    private fun startDownload(url: String, dirPath: String, fileName: String, position: Int) {
        updateItemState(position, DownloadState.PENDING)
        val downloadId =
            PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener {
                    updateItemState(position, DownloadState.DOWNLOADING)
                }
                .setOnPauseListener {
                    updateItemState(position, DownloadState.NORMAL)

                }
                .setOnCancelListener {
                    updateItemState(position, DownloadState.NORMAL)
                }
                .setOnProgressListener {
                    val progress = (it.currentBytes * 100) / it.totalBytes
                    updateItemState(position, DownloadState.DOWNLOADING, progress.toInt())
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        updateItemState(position, DownloadState.COMPLETED)
                        openFile(filesAdapter.items[position].fileItem.name.toString())
                    }
                    override fun onError(error: com.downloader.Error?) {
                        updateItemState(position, DownloadState.FAILED)
                    }
                })
    }

    private fun updateItemState(position: Int, state: DownloadState, progress: Int? = null) {
        Log.i("updateItemState", position.toString() + " - " + state.name)
        filesAdapter.items[position].downloadState = state
        progress?.let {
            filesAdapter.items[position].downloadProgress = progress
        }
        filesAdapter.notifyItemChanged(position)
    }

    companion object {
        private const val FILE_NAME = "getListOfFilesResponse.json"
    }

}