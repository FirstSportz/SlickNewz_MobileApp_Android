package com.firstsportz.slicknewz.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.adapter.NewsPagerAdapter
import com.firstsportz.slicknewz.data.model.NewsData
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import com.firstsportz.slicknewz.viewmodel.NewsViewModel
import com.firstsportz.slicknewz.viewmodel.NewsViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomTabLayout: LinearLayout
    private lateinit var appIcon: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var notificationIcon: ImageView
    private lateinit var refreshIcon: ImageView

    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "LoginPreferences"
    private val PREF_KEY_ALL_CATEGORIES = "allCategories"
    private lateinit var newsViewModel: NewsViewModel
    private var authorizationToken = "Bearer <your-token>" // Replace with actual token

    private lateinit var newsAdapter: NewsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        tabLayout = findViewById(R.id.tabLayoutCategories)
        viewPager = findViewById(R.id.viewPagerNews)
        bottomTabLayout = findViewById(R.id.customBottomNavigation)
        appIcon = findViewById(R.id.appIcon)
        searchIcon = findViewById(R.id.searchIcon)
        notificationIcon = findViewById(R.id.notificationIcon)
        refreshIcon = findViewById(R.id.refreshIcon)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", "")
        authorizationToken = "Bearer $jwt"

        // Initialize ViewModel
        val repository = AuthRepository()
        val factory = NewsViewModelFactory(repository)
        newsViewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // Set up tab layout, listeners, and custom bottom tabs
        setupTabs()
        setupTopBarListeners()
        setupCustomBottomNavigation()
    }

    private fun setupTabs() {
        val selectedCategories = getSelectedCategoriesFromPreferences()
        val categories = mutableListOf<Pair<Int?, String>>().apply {
            add(null to "Trending Today") // Default tab
            addAll(selectedCategories)
        }

        setupTabLayout(categories)
        fetchNewsForCategory(null) // Load "Trending Today" news by default
    }

    private fun getSelectedCategoriesFromPreferences(): List<Pair<Int, String>> {
        val json = sharedPreferences.getString(PREF_KEY_ALL_CATEGORIES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val categoryList = Gson().fromJson<List<Map<String, Any>>>(json, type)
            categoryList.map {
                val id = (it["id"] as Double).toInt() // Gson parses numbers as Double by default
                val name = it["name"] as String
                id to name
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun setupTabLayout(categories: List<Pair<Int?, String>>) {
        tabLayout.removeAllTabs()
        categories.forEach { (_, name) ->
            val tab = tabLayout.newTab().apply {
                text = name
            }
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: return
                val categoryId = categories[position].first
                fetchNewsForCategory(categoryId)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchNewsForCategory(categoryId: Int?) {
        newsAdapter = NewsPagerAdapter(this, isLoading = true)
        viewPager.adapter = newsAdapter

        if (categoryId == null) {
            // Fetch "Trending Today" news
            val todayDate = getCurrentDate()
            newsViewModel.fetchTrendingTodayNews(authorizationToken, todayDate)
        } else {
            // Fetch news for selected category
            newsViewModel.fetchNews(authorizationToken, listOf(categoryId))
        }

        newsViewModel.newsResponse.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.data?.let { newsList ->
                        newsAdapter.setNewsList(newsList)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message ?: "Failed to load news", Toast.LENGTH_SHORT).show()
                    newsAdapter.setNewsList(emptyList())
                }
                is Resource.Loading -> {
                    newsAdapter.setLoadingState()
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun setupTopBarListeners() {
        searchIcon.setOnClickListener {
            // Handle search icon click
        }
        notificationIcon.setOnClickListener {
            // Handle notification icon click
        }
        refreshIcon.setOnClickListener {
            // Refresh the current category
            val selectedTab = tabLayout.selectedTabPosition
            if (selectedTab >= 0) {
                val categories = getSelectedCategoriesFromPreferences()
                val categoryId = if (selectedTab == 0) null else categories[selectedTab - 1].first
                fetchNewsForCategory(categoryId)
            }
        }
        appIcon.setOnClickListener {
            // Handle app icon click
        }
    }

    private fun setupCustomBottomNavigation() {
        val tabs = listOf(
            Pair(R.id.nav_discover, Pair(R.id.icon_discover, R.id.text_discover)),
            Pair(R.id.nav_bookmarks, Pair(R.id.icon_bookmarks, R.id.text_bookmarks)),
            Pair(R.id.nav_play, Pair(R.id.icon_play, R.id.text_play)),
            Pair(R.id.nav_profile, Pair(R.id.icon_profile, R.id.text_profile))
        )


        // Default selection: Discover
        selectBottomTab(R.id.nav_discover, tabs)

        tabs.forEach { (tabId, ids) ->
            val tabLayout = findViewById<LinearLayout>(tabId)
            tabLayout.setOnClickListener {
                selectBottomTab(tabId, tabs)
            }
        }
    }

    private fun selectBottomTab(selectedTabId: Int, tabs: List<Pair<Int, Pair<Int, Int>>>) {
        tabs.forEach { (tabId, ids) ->
            val (iconId, textId) = ids
            val iconView = findViewById<ImageView>(iconId)
            val textView = findViewById<TextView>(textId)
            val tabLayout = findViewById<LinearLayout>(tabId)

            // Get the current layout params
            val layoutParams = tabLayout.layoutParams as LinearLayout.LayoutParams

            if (tabId == selectedTabId) {
                // Highlight selected tab: Show text, tint icon, set background, and adjust weight
                textView.visibility = View.VISIBLE
                tabLayout.setBackgroundResource(R.drawable.selectedtabs)
                layoutParams.weight = 2f // Set weight for selected tab
            } else {
                // Unselect other tabs: Hide text, reset icon tint, remove background, and adjust weight
                textView.visibility = View.GONE
                tabLayout.background = null
                layoutParams.weight = 1f // Set weight for unselected tabs
            }

            // Apply updated layout params
            tabLayout.layoutParams = layoutParams
        }
    }


}
