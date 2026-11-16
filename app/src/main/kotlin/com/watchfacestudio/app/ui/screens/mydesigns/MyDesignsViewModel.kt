package com.watchfacestudio.app.ui.screens.mydesigns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.repository.DesignRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyDesignsViewModel(
    private val designRepository: DesignRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val designs: StateFlow<List<DesignEntity>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            designRepository.getAllDesigns()
        } else {
            designRepository.searchDesigns(query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteDesign(id: String) {
        viewModelScope.launch {
            designRepository.deleteDesign(id)
        }
    }

    fun duplicateDesign(id: String) {
        viewModelScope.launch {
            designRepository.duplicateDesign(id)
        }
    }
}
