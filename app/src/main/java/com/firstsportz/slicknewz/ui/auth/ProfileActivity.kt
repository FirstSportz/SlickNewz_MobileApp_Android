package com.firstsportz.slicknewz.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.databinding.ActivityProfileBinding
import com.google.android.material.tabs.TabLayout

class ProfileActivity : AppCompatActivity() {


    private lateinit var darkModeToggle: Switch
    private lateinit var notificationsToggle: Switch
    private lateinit var hdcontentToggle: Switch

    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "LoginPreferences"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        darkModeToggle = findViewById<Switch>(R.id.darkModeToggle)
        notificationsToggle = findViewById<Switch>(R.id.notificationsToggle)
        hdcontentToggle = findViewById<Switch>(R.id.hdcontentToggle)


        // Load saved preferences
        darkModeToggle.isChecked = sharedPreferences.getBoolean("dark_mode", false)
        notificationsToggle.isChecked = sharedPreferences.getBoolean("enableNotification", false)
        hdcontentToggle.isChecked = sharedPreferences.getBoolean("hdcontent_enabled", false)

        checkDarkModeSettings()
        checkNotificationSettings()
        checkHdContentSettings()

        // Save preferences when toggled
        darkModeToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            checkDarkModeSettings()
        }

        notificationsToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("enableNotification", isChecked).apply()
            checkNotificationSettings()
        }


        hdcontentToggle.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("hdcontent_enabled", isChecked).apply()
            checkHdContentSettings()
        }



        setupCustomBottomNavigation()
    }

    private fun checkDarkModeSettings() {
        if(darkModeToggle.isChecked)
        {
            darkModeToggle.thumbTintList = ContextCompat.getColorStateList(this, R.color.white)
            darkModeToggle.trackTintList = ContextCompat.getColorStateList(this, R.color.primaryColor)

        }
        else{
            darkModeToggle.thumbTintList = ContextCompat.getColorStateList(this, R.color.white)
            darkModeToggle.trackTintList = ContextCompat.getColorStateList(this, R.color.gray)

        }
    }

    private fun checkNotificationSettings() {
        if(notificationsToggle.isChecked)
        {
            notificationsToggle.thumbTintList = ContextCompat.getColorStateList(this, R.color.white)
            notificationsToggle.trackTintList = ContextCompat.getColorStateList(this, R.color.primaryColor)

        }
        else{
            notificationsToggle.thumbTintList = ContextCompat.getColorStateList(this, R.color.white)
            notificationsToggle.trackTintList = ContextCompat.getColorStateList(this, R.color.gray)

        }
    }

    private fun checkHdContentSettings() {
        if(hdcontentToggle.isChecked)
        {
            hdcontentToggle.thumbTintList = ContextCompat.getColorStateList(this, R.color.white)
            hdcontentToggle.trackTintList = ContextCompat.getColorStateList(this, R.color.primaryColor)

        }
        else{
            hdcontentToggle.thumbTintList = ContextCompat.getColorStateList(this, R.color.white)
            hdcontentToggle.trackTintList = ContextCompat.getColorStateList(this, R.color.gray)

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
        selectBottomTab(R.id.nav_profile, tabs)

        tabs.forEach { (tabId, ids) ->
            val tabLayout = findViewById<LinearLayout>(tabId)
            tabLayout.setOnClickListener {
                selectBottomTab(tabId, tabs)
                when (tabId) {
                    R.id.nav_discover -> navigateToDiscoverActivity()
                    R.id.nav_bookmarks -> navigateToMyFeedActivity()
                    R.id.nav_play -> { /* Open Play content */ }
                    R.id.nav_profile -> {/* Stay on Profile activity */}
                }
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
                iconView.clearColorFilter()
                textView.visibility = View.VISIBLE
                tabLayout.setBackgroundResource(R.drawable.selectedtabs)
                layoutParams.weight = 2f // Set weight for selected tab
            } else {
                // Unselect other tabs: Hide text, reset icon tint, remove background, and adjust weight
                iconView.setColorFilter(resources.getColor(R.color.black, null))
                textView.visibility = View.GONE
                tabLayout.background = null
                layoutParams.weight = 1f // Set weight for unselected tabs
            }

            // Apply updated layout params
            tabLayout.layoutParams = layoutParams
        }
    }

    private fun navigateToDiscoverActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Smooth transition
    }

    private fun navigateToMyFeedActivity() {
        val intent = Intent(this, MyFeedActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Smooth transition
    }

}
