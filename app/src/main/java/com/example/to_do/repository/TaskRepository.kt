package com.example.to_do.repository

import android.content.Context
import android.net.Uri
import com.example.to_do.model.TaskModel

interface TaskRepository {
    fun addTask(taskModel: TaskModel, callback: (Boolean, String) -> Unit)
    fun updateTask(
        taskId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    )
    fun deleteTask(taskId: String, callback: (Boolean, String) -> Unit)

    fun deleteAllTask(callback: (Boolean, String) -> Unit)

    fun deleteCompTask(taskId: String, callback: (Boolean, String) -> Unit)

    fun moveTask(taskId: String, callback: (Boolean, String) -> Unit)

    fun moveCompTask(taskId: String, callback: (Boolean, String) -> Unit)

    fun getTaskById(taskId: String, callback: (TaskModel?, Boolean, String) -> Unit)

    fun getAllTasks(callback: (List<TaskModel>?, Boolean, String) -> Unit)



    fun getCompletedTasks(callback: (List<TaskModel>?, Boolean, String) -> Unit)

}

