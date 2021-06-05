package com.filedownloader.presentation.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.filesdownloader.R
import com.filedownloader.data.source.model.FileTypeEnum
import com.filedownloader.presentation.uimodel.DownloadState
import com.filedownloader.presentation.uimodel.FileItemUIModel
import kotlinx.android.synthetic.main.item_file.view.*

class FilesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: ArrayList<FileItemUIModel> = arrayListOf()
    val onItemClicked = MutableLiveData<FileItemUIModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FileItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_file, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FileItemViewHolder) {
            holder.bind(items[position], position, holder)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class FileItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: FileItemUIModel, position: Int, holder: FileItemViewHolder) {
            item.let {
                itemView.tvFileName.text = it.fileItem.name
                itemView.tvFileUrl.text = it.fileItem.url
                if (it.fileItem.type == FileTypeEnum.VIDEO)
                    itemView.ivFileType.setImageResource(R.drawable.ic_video_file)
                else
                    itemView.ivFileType.setImageResource(R.drawable.ic_pdf_file)
            }

            if (position == items.size - 1) {
                itemView.viewDivider.visibility = View.GONE
            } else {
                itemView.viewDivider.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                onItemClicked.value = item
            }

            when (item.downloadState) {
                DownloadState.NORMAL -> {
                    itemView.pbFileDownloadProgress.visibility = View.GONE
                    itemView.tvFileDownloadProgress.visibility = View.GONE
                    itemView.ivDownloadStatus.visibility = View.GONE
                }
                DownloadState.COMPLETED -> {
                    itemView.pbFileDownloadProgress.visibility = View.GONE
                    itemView.tvFileDownloadProgress.visibility = View.GONE
                    itemView.ivDownloadStatus.visibility = View.VISIBLE
                    itemView.ivDownloadStatus.setImageResource(R.drawable.ic_completed)
                }
                DownloadState.DOWNLOADING -> {
                    itemView.ivDownloadStatus.visibility = View.GONE
                    itemView.pbFileDownloadProgress.visibility = View.VISIBLE
                    itemView.tvFileDownloadProgress.visibility = View.VISIBLE
                    itemView.pbFileDownloadProgress.progress = item.downloadProgress?: 0
                    itemView.tvFileDownloadProgress.text = "${item.downloadProgress}%"
                }
                DownloadState.PENDING -> {
                    itemView.pbFileDownloadProgress.visibility = View.GONE
                    itemView.tvFileDownloadProgress.visibility = View.GONE
                    itemView.ivDownloadStatus.visibility = View.VISIBLE
                    itemView.ivDownloadStatus.setImageResource(R.drawable.ic_pending)
                }
                DownloadState.FAILED -> {
                    itemView.pbFileDownloadProgress.visibility = View.GONE
                    itemView.tvFileDownloadProgress.visibility = View.GONE
                    itemView.ivDownloadStatus.visibility = View.VISIBLE
                    itemView.ivDownloadStatus.setImageResource(R.drawable.ic_failed)
                }
            }
        }
    }


}