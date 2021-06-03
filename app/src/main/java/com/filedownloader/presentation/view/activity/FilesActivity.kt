package com.filedownloader.presentation.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.filedownloader.core.ViewModelFactory
import com.example.filesdownloader.R
import com.filedownloader.presentation.viewmodel.JsonFileViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class FilesActivity : AppCompatActivity() {

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
        jsonFileViewModel.readFile(FILE_NAME)
    }

    companion object {
        private const val FILE_NAME = "getListOfFilesResponse.json"
    }

}