package com.firstsportz.slicknewz.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.firstsportz.slicknewz.R
import com.firstsportz.slicknewz.data.model.Category
import com.firstsportz.slicknewz.databinding.ActivityChooseinterestBinding
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.ErrorDialog
import com.firstsportz.slicknewz.ui.utils.LoadingDialog
import com.firstsportz.slicknewz.ui.utils.Resource
import com.firstsportz.slicknewz.viewmodel.CategoryViewModel
import com.firstsportz.slicknewz.viewmodel.CategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ChooseInterestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseinterestBinding
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var loadingBar: LoadingDialog
    private lateinit var errorDialog: ErrorDialog

    private lateinit var sharedPreferences: SharedPreferences
    private var hasErrorDialogBeenShown = false
    private val selectedCategories = mutableListOf<Category>()
    private var authorizationToken = "Bearer <your-token>" // Replace with actual token
    private val PREF_NAME = "LoginPreferences"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseinterestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize utilities
        loadingBar = LoadingDialog(this)
        errorDialog = ErrorDialog(this)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jwt = sharedPreferences.getString("jwt", "")
        authorizationToken= "Bearer $jwt"
        // Set up ViewModel
        val repository = AuthRepository()
        val factory = CategoryViewModelFactory(repository)
        categoryViewModel = ViewModelProvider(this, factory)[CategoryViewModel::class.java]

        // Observe categories API response
        observeCategoriesResponse()

        // Fetch categories
        fetchCategories()

        // Set button click listener
        setButtonClickListener()

        // Set skip button click listener
        setSkipButtonClickListener()
    }

    private fun fetchCategories() {
        if (!com.firstsportz.slicknewz.utils.NetworkUtil.isInternetAvailable(this)) {
            errorDialog.showErrorDialog(message = getString(R.string.no_internet_connection))
            return
        }
        loadingBar.show(getString(R.string.loading_categories))
        categoryViewModel.fetchCategories(authorizationToken)
    }

    private fun observeCategoriesResponse() {
        categoryViewModel.categoryResponse.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    hasErrorDialogBeenShown = false
                    loadingBar.dismiss()
                    resource.data?.data?.let { displayCategories(it) }
                }
                is Resource.Error -> {
                    if (!hasErrorDialogBeenShown) {
                        hasErrorDialogBeenShown = true
                        loadingBar.dismiss()
                        errorDialog.showErrorDialog(
                            message = resource.message ?: getString(R.string.error_message)
                        )
                    }
                }
                is Resource.Loading -> loadingBar.show()
            }
        }
    }

    private fun displayCategories(categories: List<Category>) {
        val container = binding.llcategory
        container.removeAllViews()

        categories.forEach { category ->
            // Inflate the custom category layout
            val linear = LayoutInflater.from(this).inflate(
                R.layout.custom_category_checkbox, container, false
            ) as LinearLayout

            // Find and set the TextView
            val tvCategory = linear.findViewById<TextView>(R.id.tvcategory)
            tvCategory.text = category.name

            // Set background based on selection
            updateCategoryBackground(linear, category.isSelected)

            // Handle click to toggle selection
            linear.setOnClickListener {
                category.isSelected = !category.isSelected
                updateCategoryBackground(linear, category.isSelected)
                updateSelectedCategories(category)
            }

            // Add to container
            container.addView(linear)
        }
    }

    private fun updateCategoryBackground(view: LinearLayout, isSelected: Boolean) {
        if (isSelected) {
            view.setBackgroundResource(R.drawable.category_bg)
        } else {
            view.setBackgroundResource(R.drawable.categorynoselect_bg)
        }
    }

    private fun updateSelectedCategories(category: Category) {
        if (category.isSelected) {
            selectedCategories.add(category)
        } else {
            selectedCategories.remove(category)
        }
    }

    private fun setButtonClickListener() {
        binding.btnContinue.setOnClickListener {
            if (selectedCategories.isNotEmpty()) {
                val selectedCategoryNames = selectedCategories.joinToString { it.name }
                Toast.makeText(
                    this,
                    "Selected Categories: $selectedCategoryNames",
                    Toast.LENGTH_SHORT
                ).show()
                // Proceed to the next step
            } else {
                val snackbar =
                    Snackbar.make(binding.root, getString(R.string.no_categories_selected), Snackbar.LENGTH_LONG)
                snackbar.show()
                //Toast.makeText(this, getString(R.string.no_categories_selected), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSkipButtonClickListener() {
        binding.tvSkip.setOnClickListener {
           //got to next activity
        }
    }
}
