package com.firstsportz.slicknewz.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firstsportz.slicknewz.databinding.ActivityEnablenotificationBinding
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog


class EnableNotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnablenotificationBinding
    private lateinit var errorDialog: ErrorDialog
    private lateinit var loadingBar: LoadingDialog
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "LoginPreferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnablenotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize dialogs
        errorDialog = ErrorDialog(this)
        loadingBar = LoadingDialog(this)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Set button click listener
        binding.btnEnableNotifications.setOnClickListener {
            enableNotifications()
        }



    }

    private fun enableNotifications() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("enableNotification",true)
        editor.apply()
        navigateToEnableelcomeActivity()
    }

    private fun navigateToEnableelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }




}
