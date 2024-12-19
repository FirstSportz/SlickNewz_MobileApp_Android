package com.firstsportz.slicknewz.animation

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class HingePageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        // Set the pivot point to the top-left corner
        page.pivotX = 0f
        page.pivotY = 0f

        when {
            position < -1 -> {
                // Page is off-screen to the left
                page.alpha = 0f
            }
            position <= 1 -> {
                // Position varies from [-1, 1] during swipe
                page.alpha = 1f - abs(position) // Fade-out effect
                page.rotation = -20 * position // Rotate around the hinge
                page.scaleX = 1f - 0.1f * abs(position) // Scale slightly
                page.scaleY = 1f - 0.1f * abs(position)
            }
            else -> {
                // Page is off-screen to the right
                page.alpha = 0f
            }
        }
    }
}
