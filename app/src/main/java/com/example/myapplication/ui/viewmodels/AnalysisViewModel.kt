package com.example.myapplication.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.PracticeSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).practiceSessionDao()

    val sessions: StateFlow<List<PracticeSession>> = dao.getAllSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun insertSession(session: PracticeSession) {
        viewModelScope.launch {
            dao.insertSession(session)
        }
    }

    fun deleteSession(id: Int) {
        viewModelScope.launch {
            dao.deleteSession(id)
        }
    }
}
