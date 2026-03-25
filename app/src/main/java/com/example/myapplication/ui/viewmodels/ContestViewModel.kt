package com.example.myapplication.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.Contest
import com.example.myapplication.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContestViewModel : ViewModel() {

    private val _contests = MutableStateFlow<List<Contest>>(emptyList())
    val contests: StateFlow<List<Contest>> = _contests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchContests()
    }

    fun fetchContests() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _contests.value = RetrofitClient.instance.getUpcomingContests()
            } catch (e: Exception) {
                _error.value = "Failed to load contests: ${e.message ?: "Network or server error"}"
                _contests.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
