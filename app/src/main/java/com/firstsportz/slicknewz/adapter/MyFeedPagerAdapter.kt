package com.firstsportz.slicknewz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.data.model.NewsData
import com.firstsportz.slicknewz.ui.auth.DashboardActivity
import com.firstsportz.slicknewz.ui.auth.MyFeedActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyFeedPagerAdapter(
    private val activity: MyFeedActivity,
    private var newsList: List<NewsData> = emptyList(),
    private var isLoading: Boolean = true // Initial state is loading
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_LOADING = 0
        private const val TYPE_CONTENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) TYPE_LOADING else TYPE_CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LOADING) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news_card, parent, false)
            NewsViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsViewHolder && !isLoading) {
            holder.bind(newsList[position])
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) 1 else newsList.size
    }

    fun setNewsList(newsList: List<NewsData>) {
        this.newsList = newsList
        this.isLoading = false
        notifyDataSetChanged()
    }

    fun setLoadingState() {
        this.isLoading = true
        notifyDataSetChanged()
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsTitle: TextView = itemView.findViewById(R.id.textNewsTitle)
        private val newsDateTime: TextView = itemView.findViewById(R.id.textNewsDateTime)
        private val newsImage: ImageView = itemView.findViewById(R.id.imageNews)
        private val menuOptions: ImageView = itemView.findViewById(R.id.menuOptions)

        fun bind(newsData: NewsData) {
            newsTitle.text = newsData.title

            Glide.with(itemView.context)
                .load(newsData.cover.url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(newsImage)

            val (relativeDate, formattedDate) = newsData.publishedAt?.let { getFormattedAndRelativeTime(it) }
                ?: "N/A" to "N/A"
            newsDateTime.text = "$relativeDate • $formattedDate"

            menuOptions.setOnClickListener {
                showBottomSheetOptions(newsData)
            }
        }

        private fun showBottomSheetOptions(newsData: NewsData) {
            val bottomSheetDialog = BottomSheetDialog(activity)
            val bottomSheetView = LayoutInflater.from(activity).inflate(
                R.layout.bottom_sheet_menu, null
            )

            bottomSheetView.findViewById<TextView>(R.id.optionShare).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionEmbed).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionBookmark).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionSaveGallery).setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionHidePost).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }

        private fun getFormattedAndRelativeTime(publishedAt: String): Pair<String, String> {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("hh:mm a z", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault()

            return try {
                val publishedDate = inputFormat.parse(publishedAt)
                val now = Date()
                val diff = now.time - (publishedDate?.time ?: now.time)

                val relativeTime = when {
                    TimeUnit.MILLISECONDS.toMinutes(diff) < 60 -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes ago"
                    TimeUnit.MILLISECONDS.toHours(diff) < 24 -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
                    TimeUnit.MILLISECONDS.toDays(diff) == 1L -> "Yesterday"
                    TimeUnit.MILLISECONDS.toDays(diff) < 7 -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
                    TimeUnit.MILLISECONDS.toDays(diff) < 30 -> "${TimeUnit.MILLISECONDS.toDays(diff) / 7} weeks ago"
                    TimeUnit.MILLISECONDS.toDays(diff) < 365 -> "${TimeUnit.MILLISECONDS.toDays(diff) / 30} months ago"
                    else -> "${TimeUnit.MILLISECONDS.toDays(diff) / 365} years ago"
                }

                val formattedTime = publishedDate?.let { outputFormat.format(it) } ?: "N/A"
                relativeTime to formattedTime
            } catch (e: Exception) {
                "N/A" to "N/A"
            }
        }
    }
}
