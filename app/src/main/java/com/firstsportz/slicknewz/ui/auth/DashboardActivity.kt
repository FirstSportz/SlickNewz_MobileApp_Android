package com.firstsportz.slicknewz.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.adapter.NewsPagerAdapter
import com.firstsportz.slicknewz.animation.HingePageTransformer
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        tabLayout = findViewById(R.id.tabLayoutCategories)
        viewPager = findViewById(R.id.viewPagerNews)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // Set up ViewPager2 with Adapter
        val newsAdapter = NewsPagerAdapter(this)
        viewPager.adapter = newsAdapter
        viewPager.setPageTransformer(HingePageTransformer())

        // Add categories to TabLayout
        val categories = listOf("Sports", "Politics", "Trending Today", "Hollywood")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = categories[position]
        }.attach()

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
    }
}
