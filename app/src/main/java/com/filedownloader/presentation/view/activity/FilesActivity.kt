package com.filedownloader.presentation.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.Status
import com.example.filesdownloader.R
import com.filedownloader.core.ViewModelFactory
import com.filedownloader.presentation.uimodel.mapFileItemToUIModel
import com.filedownloader.presentation.view.adapter.FilesAdapter
import com.filedownloader.presentation.viewmodel.JsonFileViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_files.*
import java.io.File
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
        filesAdapter.onItemClicked.observe(this, {
            Log.i("onItemClicked", it.fileItem.name)
            if (PRDownloader.getStatus(0) == Status.COMPLETED) {

            }
//            val dirPath = android.os.Environment.getExternalStorageDirectory().absolutePath +
//                    File.separator+"downloads";
            val dirPath = filesDir.absolutePath + File.separator + "downloads"

            startDownload(it.fileItem.url, dirPath, it.fileItem.name, filesAdapter.items.indexOf(it))
        })
    }

    private fun startDownload(url: String, dirPath: String, fileName: String, position: Int) {
        val downloadId =
            PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener { }
                .setOnPauseListener { }
                .setOnCancelListener { }
                .setOnProgressListener {
                    val percentage = (it.currentBytes * 100) / it.totalBytes
                    Log.i("onItemClicked", percentage.toString())
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        Log.i("onItemClicked", "onDownloadComplete")
                    }
                    override fun onError(error: com.downloader.Error?) {
                        Log.i("onItemClicked", error.toString())
                    }
                })

    }


    companion object {
        private const val FILE_NAME = "getListOfFilesResponse.json"
    }

}