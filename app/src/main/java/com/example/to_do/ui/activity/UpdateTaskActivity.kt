package com.example.to_do.ui.activity

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.to_do.R
import com.example.to_do.databinding.ActivityUpdateTaskBinding
import com.example.to_do.repository.TaskRepositoryImpl
import com.example.to_do.viewmodel.TaskViewModel

class UpdateTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateTaskBinding
    lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo= TaskRepositoryImpl()
        taskViewModel = TaskViewModel(repo)

        var id:String=intent.getStringExtra("TaskId").toString()
        taskViewModel.getTaskById(id)

        binding.UpdateTaskDate.setOnClickListener {
            showDatePicker()
        }

        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_options, // Reference to the priority options in strings.xml
            android.R.layout.simple_spinner_item
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = priorityAdapter

        // Set up the Category Spinner
        val categoryAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.category_options, // Reference to the category options in strings.xml
            android.R.layout.simple_spinner_item
        )

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        taskViewModel.tasks.observe(this){
            binding.UpdateTaskName.setText(it?.TaskName.toString())
            binding.UpdateTaskDate.setText(it?.TaskDate.toString())
            binding.UpdateTaskDescription.setText(it?.TaskDescription.toString())
            binding.spinnerPriority.selectedItem.toString()
            binding.spinnerCategory.selectedItem.toString()

        }


        binding.Updatebutton.setOnClickListener {
            val newTaskName = binding.UpdateTaskName.text.toString()
            val newTaskDate=binding.UpdateTaskDate.text.toString()
            val newTaskDesc=binding.UpdateTaskDescription.text.toString()
            val newTaskPriority=binding.spinnerPriority.selectedItem.toString()
            val newTaskCategory=binding.spinnerCategory.selectedItem.toString()


            var updateMap= mutableMapOf<String,Any>()
            updateMap["taskName"]=newTaskName
            updateMap["taskDate"]=newTaskDate
            updateMap["taskDescription"]=newTaskDesc
            updateMap["selectedPriority"]=newTaskPriority
            updateMap["selectedCategory"]=newTaskCategory

            taskViewModel.updateTask(id,updateMap){
                    success,message ->
                if(success){
                    Toast.makeText(this@UpdateTaskActivity ,message, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@UpdateTaskActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.submitButton)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Formatting the date
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.UpdateTaskDate.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
