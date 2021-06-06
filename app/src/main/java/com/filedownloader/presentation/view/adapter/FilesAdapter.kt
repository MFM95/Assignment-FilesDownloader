package com.filedownloader.presentation.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.filesdownloader.R
import com.filedownloader.data.source.model.FileTypeEnum
import com.filedownloader.presentation.uimodel.DownloadState
import com.filedownloader.presentation.uimodel.FileItemUIModel
import kotlinx.android.synthetic.main.item_file.view.*

class FilesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var items: ArrayList<FileItemUIModel> = arrayListOf()
    val onItemClicked = MutableLiveData<FileItemUIModel>()

    var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FileItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_file, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FileItemViewHolder) {
            tracker?.let {
                holder.bind(items[position], position, holder, it.isSelected(position.toLong()))
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class FileItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: FileItemUIModel, position: Int, holder: FileItemViewHolder, isActivated: Boolean = false) {
            itemView.isActivated = isActivated
            item.let {
                itemView.tvFileName.text = it.fileItem.name
                itemView.tvFileUrl.text = it.fileItem.url
                if (it.fileItem.type == FileTypeEnum.VIDEO)
                    itemView.ivFileType.setImageResource(R.drawable.ic_video2)
                else
                    itemView.ivFileType.setImageResource(R.drawable.ic_pdf)
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
                    itemView.ivDownloadStatus.setImageResource(R.drawable.ic_done)
                }
                DownloadState.DOWNLOADING -> {
                    itemView.ivDownloadStatus.visibility = View.GONE
                    itemView.pbFileDownloadProgress.visibility = View.VISIBLE
                    itemView.tvFileDownloadProgress.visibility = View.VISIBLE
                    itemView.pbFileDownloadProgress.progress = item.downloadProgress ?: 0
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
                    itemView.ivDownloadStatus.setImageResource(R.drawable.ic_error)
                }
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = bindingAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }
    }

    class MyItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view)
                        as FilesAdapter.FileItemViewHolder).getItemDetails()
            }
            return null

        }
    }

}