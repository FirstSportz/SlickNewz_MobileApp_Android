package com.firstsportz.slicknewz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstsportz.slicknewz.data.model.NewsResponse
import com.firstsportz.slicknewz.repository.AuthRepository
import com.firstsportz.slicknewz.ui.utils.Resource
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _newsResponse = MutableLiveData<Resource<NewsResponse>>()
    val newsResponse: LiveData<Resource<NewsResponse>> = _newsResponse

    fun fetchNews(authorization: String, categoryIds: List<Int>) {
        val queryParams = categoryIds.joinToString("&") { "filters[category][id][\$in][]=$it" } // Escape `$in`
        val fullUrl = "api/articles?populate=*&$queryParams"

        viewModelScope.launch {
            _newsResponse.postValue(Resource.Loading())
            try {
                val response = repository.fetchNews(authorization, fullUrl)
                if (response.isSuccessful && response.body() != null) { // Check for null response body
                    _newsResponse.postValue(Resource.Success(response.body()!!))
                } else {
                    _newsResponse.postValue(Resource.Error(response.message() ?: "Unknown error occurred"))
                }
            } catch (e: Exception) {
                _newsResponse.postValue(Resource.Error(e.message ?: "Unknown error occurred")) // Handle nullable `e.message`
            }
        }
    }

    fun fetchTrendingTodayNews(authorization: String, startDate: String) {
        val queryParams = "filters[publishedAt][\$gte]=$startDate"
        val fullUrl = "api/articles?populate=*&$queryParams"
        viewModelScope.launch {
            _newsResponse.postValue(Resource.Loading())
            try {
                val response = repository.fetchNews(authorization, fullUrl)
                if (response.isSuccessful && response.body() != null) { // Check for null response body
                    _newsResponse.postValue(Resource.Success(response.body()!!))
                } else {
                    _newsResponse.postValue(Resource.Error(response.message() ?: "Unknown error occurred"))
                }
            } catch (e: Exception) {
                _newsResponse.postValue(Resource.Error(e.message ?: "Unknown error occurred")) // Handle nullable `e.message`
            }
        }
    }

}
