package com.firstsportz.slicknewz.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.adapter.NewsPagerAdapter
import com.firstsportz.slicknewz.data.model.NewsData
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.LoadingDialog
import com.firstsportz.slicknewz.ui.utils.Resource
import com.firstsportz.slicknewz.viewmodel.NewsViewModel
import com.firstsportz.slicknewz.viewmodel.NewsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var appIcon: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var notificationIcon: ImageView
    private lateinit var refreshIcon: ImageView
    private lateinit var loadingBar: LoadingDialog

    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "LoginPreferences"
    private val PREF_KEY_SELECTED_CATEGORIES = "selectedCategories"
    private lateinit var newsViewModel: NewsViewModel
    private var authorizationToken = "Bearer <your-token>" // Replace with actual token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        tabLayout = findViewById(R.id.tabLayoutCategories)
        viewPager = findViewById(R.id.viewPagerNews)
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        appIcon = findViewById(R.id.appIcon)
        searchIcon = findViewById(R.id.searchIcon)
        notificationIcon = findViewById(R.id.notificationIcon)
        refreshIcon = findViewById(R.id.refreshIcon)

        loadingBar = LoadingDialog(this)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", "")
        authorizationToken = "Bearer $jwt"

        // Initialize ViewModel
        val repository = AuthRepository()
        val factory = NewsViewModelFactory(repository)
        newsViewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // Set up tabs and listeners
        setupTopBarListeners()
        setupTabs()
        setupBottomNavigation()
    }

    private fun setupTabs() {
        val selectedCategories = getSelectedCategoriesFromPreferences()
        val categories = mutableListOf<Pair<Int?, String>>().apply {
            add(null to "Trending Today") // Add default "Trending Today" tab
            addAll(selectedCategories)
        }

        setupTabLayout(categories)
        fetchNewsForCategory(null) // Fetch news for "Trending Today" by default
    }

    private fun getSelectedCategoriesFromPreferences(): List<Pair<Int, String>> {
        val json = sharedPreferences.getString(PREF_KEY_SELECTED_CATEGORIES, null) ?: return emptyList()
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<Map<String, Any>>>() {}.type
            val categoryList = com.google.gson.Gson().fromJson<List<Map<String, Any>>>(json, type)
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
                    loadingBar.dismiss()
                    resource.data?.data?.let { newsList ->
                        setupViewPager(newsList)
                    }
                }
                is Resource.Error -> {
                    loadingBar.dismiss()
                    Toast.makeText(this, resource.message ?: "Failed to load news", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> loadingBar.show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(calendar.time)
    }

    private fun setupViewPager(newsList: List<NewsData>) {
        val newsAdapter = NewsPagerAdapter(this, newsList)
        viewPager.adapter = newsAdapter
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

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            updateBottomNavigationLabels(item.itemId)
            when (item.itemId) {
                R.id.nav_discover -> { /* Handle Discover */ true }
                R.id.nav_bookmarks -> { /* Handle Bookmarks */ true }
                R.id.nav_play -> { /* Handle Play */ true }
                R.id.nav_profile -> { /* Handle Profile */ true }
                else -> false
            }
        }
        // Initialize labels based on default selected item
        bottomNavigationView.selectedItemId = R.id.nav_discover
        updateBottomNavigationLabels(R.id.nav_discover)
    }

    private fun updateBottomNavigationLabels(selectedItemId: Int) {
        val menu = bottomNavigationView.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            if (menuItem.itemId == selectedItemId) {
                menuItem.titleCondensed = menuItem.title.toString() // Show icon + text for selected
            } else {
                menuItem.titleCondensed = "" // Show only icon for others
            }
        }
    }

}
