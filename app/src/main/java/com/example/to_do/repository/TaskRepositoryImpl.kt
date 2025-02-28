package com.example.to_do.repository


import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.utils.ObjectUtils
import com.example.to_do.model.TaskModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import com.cloudinary.Cloudinary
import java.util.concurrent.Executors


class TaskRepositoryImpl:TaskRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("tasks")

    override fun addTask(taskModel: TaskModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: return callback(false, "Failed to generate task ID")
        taskModel.TaskId = id

        ref.child(id).setValue(taskModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Task added successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error")
            }
        }
    }


    override fun updateTask(taskId: String, data: MutableMap<String, Any>, callback: (Boolean, String) -> Unit) {
        ref.child(taskId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Task updated successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error")
            }
        }
    }

    override fun deleteTask(taskId: String, callback: (Boolean, String) -> Unit) {
        ref.child(taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Task deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error")
            }
        }
    }


    override fun deleteAllTask(callback: (Boolean, String) -> Unit) {
        val tasksRef = database.reference.child("tasks")
        val completedTasksRef = database.reference.child("completedTasks")

        tasksRef.removeValue().addOnCompleteListener { tasksResult ->
            completedTasksRef.removeValue().addOnCompleteListener { completedTasksResult ->
                if (tasksResult.isSuccessful && completedTasksResult.isSuccessful) {
                    callback(true, "All tasks deleted successfully")
                } else {
                    val errorMessage = tasksResult.exception?.message ?: completedTasksResult.exception?.message ?: "Unknown error"
                    callback(false, errorMessage)
                }
            }
        }
    }




    override fun deleteCompTask(taskId: String, callback: (Boolean, String) -> Unit) {
        val completedRef = database.reference.child("completedTasks").child(taskId)
        completedRef.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Completed task deleted successfully")
            } else {
                callback(false, it.exception?.message ?: "Unknown error")
            }
        }
    }

    override fun moveTask(taskId: String, callback: (Boolean, String) -> Unit) {
        ref.child(taskId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val completedRef = database.reference.child("completedTasks").child(taskId)
                    completedRef.setValue(snapshot.value).addOnCompleteListener { moveTaskResult ->
                        if (moveTaskResult.isSuccessful) {
                            ref.child(taskId).removeValue().addOnCompleteListener { deleteTaskResult ->
                                if (deleteTaskResult.isSuccessful) {
                                    callback(true, "Task moved successfully")
                                } else {
                                    callback(false, deleteTaskResult.exception?.message ?: "Unknown error")
                                }
                            }
                        } else {
                            callback(false, moveTaskResult.exception?.message ?: "Unknown error")
                        }
                    }
                } else {
                    callback(false, "Task not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message)
            }
        })
    }

    override fun moveCompTask(taskId: String, callback: (Boolean, String) -> Unit) {
        val completedRef = database.reference.child("completedTasks").child(taskId) // Get from completedTasks
        completedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val taskRef = database.reference.child("tasks").child(taskId) // Move back to tasks
                    taskRef.setValue(snapshot.value).addOnCompleteListener { moveTaskResult ->
                        if (moveTaskResult.isSuccessful) {
                            completedRef.removeValue().addOnCompleteListener { deleteTaskResult ->
                                if (deleteTaskResult.isSuccessful) {
                                    callback(true, "Task moved back to tasks successfully")
                                } else {
                                    callback(false, deleteTaskResult.exception?.message ?: "Unknown error")
                                }
                            }
                        } else {
                            callback(false, moveTaskResult.exception?.message ?: "Unknown error")
                        }
                    }
                } else {
                    callback(false, "Task not found in completed tasks")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message)
            }
        })
    }



    override fun getTaskById(taskId: String, callback: (TaskModel?, Boolean, String) -> Unit) {
        ref.child(taskId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val model = snapshot.getValue(TaskModel::class.java)
                    callback(model, true, "Task fetched successfully")
                } else {
                    callback(null, false, "Task not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun getAllTasks(callback: (List<TaskModel>?, Boolean, String) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = mutableListOf<TaskModel>()
                for (eachData in snapshot.children) {
                    val model = eachData.getValue(TaskModel::class.java)
                    model?.let { tasks.add(it) }
                }
                callback(tasks, true, "Tasks fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }



    override fun getCompletedTasks(callback: (List<TaskModel>?, Boolean, String) -> Unit) {
        val completedRef = database.reference.child("completedTasks") // Fetch directly from completedTasks
        completedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = mutableListOf<TaskModel>()
                for (eachData in snapshot.children) {
                    val model = eachData.getValue(TaskModel::class.java)
                    model?.let { tasks.add(it) }
                }
                callback(tasks, true, "Completed tasks fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

}

