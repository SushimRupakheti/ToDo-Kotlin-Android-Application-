package com.example.to_do.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.to_do.model.TaskModel
import com.example.to_do.repository.TaskRepository


class TaskViewModel(private val repo: TaskRepository) : ViewModel() {
    fun addTask(taskModel: TaskModel, callback:(Boolean, String)->Unit){
        repo.addTask(taskModel,callback)
    }
    fun updateTask(taskId:String,data:MutableMap<String,Any>,callback:(Boolean,String)->Unit){
        repo.updateTask(taskId,data){ success, message ->
            if (success) {
                getAllTasks() // Refresh tasks after update
            }
            callback(success, message)
        }
    }
    fun deleteTask(taskId:String,callback:(Boolean,String)->Unit){
        repo.deleteTask(taskId,callback)
    }





    var _tasks= MutableLiveData<TaskModel?>()
    var tasks= MutableLiveData<TaskModel?>()
        get()=_tasks

    var _allTasks= MutableLiveData<List<TaskModel>>()
    var allTasks= MutableLiveData<List<TaskModel>>()
        get()=_allTasks

    fun getTaskById(taskId:String){
        repo.getTaskById(taskId){
                model,success,message->
            if (success){
                _tasks.value=model
            }
        }
    }


    var _loading= MutableLiveData<Boolean>()
    var loading= MutableLiveData<Boolean>()
        get()=_loading

    fun getAllTasks(){
        repo.getAllTasks {product,success,message->
            if (success){
                _allTasks.value=product
                _loading.value = false
            }
        }
    }


    fun moveTask(taskId: String, callback: (Boolean, String) -> Unit) {
        loading.value = true
        repo.moveTask(taskId) { success, message ->
            loading.value = false
            if (success) {
                getAllTasks() // Refresh tasks
                getCompletedTasks() // Refresh completed tasks
            }
            callback(success, message)
        }
    }


    fun moveCompTask(taskId: String, callback: (Boolean, String) -> Unit) {
        loading.value = true
        repo.moveCompTask(taskId) { success, message ->
            loading.value = false
            if (success) {
                getAllTasks() // Refresh tasks
                getCompletedTasks() // Refresh completed tasks
            }
            callback(success, message)
        }
    }




    private val _completedTasks = MutableLiveData<List<TaskModel>>()
    val completedTasks: LiveData<List<TaskModel>> get() = _completedTasks

    fun deleteCompTask(taskId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteCompTask(taskId, callback) // Ensure repo calls delete from "completedTasks"
    }

    fun getCompletedTasks() {
       repo.getCompletedTasks { tasks, success, _ ->
            if (success) {
                _completedTasks.value = tasks
            }
        }
    }

    fun deleteAllTasks(callback: (Boolean, String) -> Unit) {
        repo.deleteAllTask(callback)
    }





}

