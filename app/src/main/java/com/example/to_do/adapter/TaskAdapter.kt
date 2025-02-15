package com.example.to_do.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do.R
import com.example.to_do.model.TaskModel
import com.example.to_do.ui.activity.UpdateTaskActivity

class TaskAdapter (
    val context: Context,
    var data:ArrayList<TaskModel>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tName: TextView = itemView.findViewById(R.id.TaskName)
        val tDate: TextView = itemView.findViewById(R.id.TaskDate)
        val tDescription: TextView = itemView.findViewById(R.id.TaskDescription)
        val tPriority: TextView = itemView.findViewById(R.id.TaskPriority)
        val tCategory: TextView = itemView.findViewById(R.id.TaskCategory)
        val tupdate:TextView=itemView.findViewById(R.id.updateButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_tasks, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.tName.text = data[position].TaskName
        holder.tDate.text = data[position].TaskDate
        holder.tDescription.text = data[position].TaskDescription
        holder.tPriority.text=data[position].TaskPriority
        holder.tCategory.text=data[position].TaskCategory


        holder.tupdate.setOnClickListener{
            var intent = Intent(context, UpdateTaskActivity::class.java)

            intent.putExtra("TaskId", data[position].TaskId)
            context.startActivity(intent)
        }
    }


    fun updateData(newTasks: List<TaskModel>) {
        data.clear()
        data.addAll(newTasks)
        notifyDataSetChanged()
    }

    fun getTaskID(position: Int):String{
        return data[position].TaskId
    }

                   }