package com.firstsportz.slicknewz.ui.auth

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.adapter.NewsPagerAdapter
import com.firstsportz.slicknewz.animation.HingePageTransformer
import com.firstsportz.slicknewz.data.model.Category
import com.firstsportz.slicknewz.data.model.News

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var appIcon: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var notificationIcon: ImageView
    private lateinit var refreshIcon: ImageView

    private val categories = listOf(
        Category(
            1,
            "hf7gqxzrey01rxxnx7kc3umr",
            "Sports",
            "sports",
            "This category is for sports.",
            "2024-10-15T17:43:04.990Z",
            "",
            "",
            false,
            listOf(
                News("Formula 1 News", "This is Formula 1 news description."),
                News("Cricket Highlights", "Top cricket matches highlights."),
                News("Football Insights", "Analysis of recent football matches.")
            )
        ),
        Category(
            2,
            "ufv8mj4hmxdg9t9jwy42enf8",
            "Politics",
            "politics",
            "This category is for politics.",
            "2024-10-15T17:44:07.737Z",
            "",
            "",
            false,
            listOf(
                News("Election Updates", "Latest election updates."),
                News("Political Scandals", "Biggest political scandals revealed."),
                News("Parliament Insights", "Inside look into parliament discussions.")
            )
        ),
        Category(
            3,
            "azokgigcmov2nr4e0l5dx0cu",
            "Trending Today",
            "trending-today",
            "This category is for trending topics.",
            "2024-10-15T17:44:36.911Z",
            "",
            "",
            false,
            listOf(
                News("Breaking News", "Major headlines of the day."),
                News("Viral Trends", "What's trending online today."),
                News("World News", "Key global updates.")
            )
        ),
        Category(
            4,
            "depnlquck8fufgft5kgqoey5",
            "Hollywood",
            "hollywood",
            "This category is for Hollywood updates.",
            "2024-10-15T17:44:55.002Z",
            "",
            "",
            false,
            listOf(
                News("Celebrity Gossip", "Latest gossip from Hollywood."),
                News("Movie Releases", "Upcoming movie releases."),
                News("Award Shows", "Highlights from award shows.")
            )
        )
    )

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

        setupTabLayout()
        setupViewPager(categories[0].news) // Load news for the first category by default

        // Handle Tab Clicks
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    val selectedCategory = categories[position]
                    setupViewPager(selectedCategory.news)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_discover -> { /* Handle Discover */ true }
                R.id.nav_bookmarks -> { /* Handle Bookmarks */ true }
                R.id.nav_play -> { /* Handle Play */ true }
                R.id.nav_profile -> { /* Handle Profile */ true }
                else -> false
            }
        }

        // Top bar icon listeners
        setupTopBarListeners()
    }

    private fun setupTabLayout() {
        categories.forEach { category ->
            val tab = tabLayout.newTab().apply {
                text = category.name // Use the `name` property of `Category`
            }
            tabLayout.addTab(tab)
        }
    }

    private fun setupViewPager(newsList: List<News>) {
        val newsAdapter = NewsPagerAdapter(this, newsList)
        viewPager.adapter = newsAdapter
        viewPager.setPageTransformer(HingePageTransformer())
    }

    private fun setupTopBarListeners() {
        searchIcon.setOnClickListener {
            // Handle search icon click
        }
        notificationIcon.setOnClickListener {
            // Handle notification icon click
        }
        refreshIcon.setOnClickListener {
            // Handle refresh icon click
        }
        appIcon.setOnClickListener {
            // Handle app icon click
        }
    }
}
