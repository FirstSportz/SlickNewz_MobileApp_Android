package com.firstsportz.slicknewz

import android.app.Application
import com.google.firebase.FirebaseApp

class SlickNewzApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
