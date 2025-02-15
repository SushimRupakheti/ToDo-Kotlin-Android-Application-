package com.example.to_do.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.to_do.repository.TaskRepositoryImpl

class TaskViewModelFactory (private val taskRepository: TaskRepositoryImpl) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(taskRepository) as T
    }
}
