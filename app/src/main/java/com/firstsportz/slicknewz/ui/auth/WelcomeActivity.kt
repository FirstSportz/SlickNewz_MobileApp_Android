package com.firstsportz.slicknewz.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firstsportz.slicknewz.databinding.ActivityAccountIsReadyBinding
import com.firstsportz.slicknewz.databinding.ActivityEnablenotificationBinding
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog


class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountIsReadyBinding
    private lateinit var errorDialog: ErrorDialog
    private lateinit var loadingBar: LoadingDialog
    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_NAME = "LoginPreferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountIsReadyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize dialogs
        errorDialog = ErrorDialog(this)
        loadingBar = LoadingDialog(this)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        binding.tvUsername.text = userName
        binding.tvTagname.text = "@"+userName
        // Set button click listener
        binding.btnDiscover.setOnClickListener {
            val editor = sharedPreferences.edit()

            if(!sharedPreferences.contains("isProfileComplete"))
                editor.putBoolean("isProfileComplete",true)
            editor.apply()

            navigateToDashboardScreen()
        }



    }


    private fun navigateToDashboardScreen() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Ensure the current activity is finished
    }



}
