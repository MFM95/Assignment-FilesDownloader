package com.filedownloader.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.filedownloader.core.BaseViewModel
import com.filedownloader.data.source.model.FileItem
import com.filedownloader.domain.interactor.ParseJsonFileUseCase
import com.filedownloader.domain.interactor.ReadFileUseCase
import com.filedownloader.presentation.viewmodel.JsonFileViewModel_Factory.create
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject
import kotlin.collections.ArrayList

class JsonFileViewModel @Inject constructor(
    private val readFileUseCase: ReadFileUseCase,
    private val parseJsonFileUseCase: ParseJsonFileUseCase
) :
    BaseViewModel() {

    val filesLiveData by lazy { MutableLiveData<ArrayList<FileItem>>() }
    val errorLiveData by lazy { MutableLiveData<Throwable>() }

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



//    fun download(url: String, file: File): Observable<Int> {
//        val okHttpBuilder = okHttpClient.newBuilder()
//            .connectTimeout(HTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
//            .readTimeout(HTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
//        this.okHttpClient = okHttpBuilder.build()
//        return Observable.create<Int> { emitter ->
//            val request = Request.Builder().url(url).build()
//            val response = okHttpClient.newCall(request).execute()
//            val body = response.body
//            val responseCode = response.code
//            if (responseCode >= HttpURLConnection.HTTP_OK &&
//                responseCode < HttpURLConnection.HTTP_MULT_CHOICE &&
//                body != null) {
//                val length = body.contentLength()
//                body.byteStream().apply {
//                    file.outputStream().use { fileOut ->
//                        var bytesCopied = 0
//                        val buffer = ByteArray(BUFFER_LENGTH_BYTES)
//                        var bytes = read(buffer)
//                        while (bytes >= 0) {
//                            fileOut.write(buffer, 0, bytes)
//                            bytesCopied += bytes
//                            bytes = read(buffer)
//                            emitter.onNext(
//                                ((bytesCopied * 100)/length).toInt())
//                        }
//                    }
//                    emitter.onComplete()
//                }
//            } else {
//                // Report the error
//            }
//        }
//    }
}