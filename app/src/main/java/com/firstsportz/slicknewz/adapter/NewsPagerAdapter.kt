package com.firstsportz.slicknewz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.ui.auth.DashboardActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class NewsPagerAdapter(private val activity: DashboardActivity) :
    RecyclerView.Adapter<NewsPagerAdapter.NewsViewHolder>() {

    private val newsList = listOf(
        "Formula 1 News",
        "Business Courses",
        "Latest Hollywood Buzz",
        "Sports Highlights"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }

    override fun getItemCount() = newsList.size

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsTitle: TextView = itemView.findViewById(R.id.textNewsTitle)
        private val newsImage: ImageView = itemView.findViewById(R.id.imageNews)
        private val menuOptions: ImageView = itemView.findViewById(R.id.menuOptions)

        fun bind(news: String) {
            newsTitle.text = news
            // Set a placeholder image
            newsImage.setImageResource(R.drawable.news)

            // Handle menu options click
            menuOptions.setOnClickListener {
                showBottomSheetOptions()
            }
        }

        private fun showBottomSheetOptions() {
            val bottomSheetDialog = BottomSheetDialog(activity)
            val bottomSheetView = LayoutInflater.from(activity).inflate(
                R.layout.bottom_sheet_menu,
                null
            )

            bottomSheetView.findViewById<TextView>(R.id.optionShare).setOnClickListener {
                // Handle share logic
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionEmbed).setOnClickListener {
                // Handle embed logic
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionBookmark).setOnClickListener {
                // Handle add to bookmark logic
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionSaveGallery).setOnClickListener {
                // Handle save to gallery logic
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextView>(R.id.optionHidePost).setOnClickListener {
                // Handle hide post logic
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }
}
