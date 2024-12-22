package com.firstsportz.slicknewz.custom

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

object ViewPagerUtils {
    fun disableSwipe(viewPager: ViewPager2) {
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.setOnTouchListener { _, _ -> true } // Consume touch events
    }

    fun enableSwipe(viewPager: ViewPager2) {
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.setOnTouchListener(null) // Allow touch events
    }
}
