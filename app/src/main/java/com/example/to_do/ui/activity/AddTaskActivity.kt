package com.example.to_do.ui.activity

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.to_do.R
import com.example.to_do.databinding.ActivityAddTaskBinding
import com.example.to_do.model.TaskModel
import com.example.to_do.repository.TaskRepositoryImpl
import com.example.to_do.viewmodel.TaskViewModel

class AddTaskActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddTaskBinding
    lateinit var taskViewModel:TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo= TaskRepositoryImpl()
        taskViewModel= TaskViewModel(repo)

        binding.UpdateTaskDate.setOnClickListener {
            showDatePicker()
        }
        // Set up the Priority Spinner
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

        binding.spinnerPriority.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        })

        binding.spinnerCategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                if (position == 0) { // If the hint is selected, reset to 0
                    Toast.makeText(this@AddTaskActivity, "Please select a valid category", Toast.LENGTH_SHORT).show()
                    binding.spinnerCategory.setSelection(0)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        })

        binding.submitButton.setOnClickListener{
            var taskName=binding.UpdateTaskName.text.toString()
            var taskDate=binding.UpdateTaskDate.text.toString()
            var taskDescription=binding.UpdateDescription.text.toString()
            val selectedPriority = binding.spinnerPriority.selectedItem.toString()?:"Not Selected"
            val selectedCategory = binding.spinnerCategory.selectedItem.toString()?:"Not Selected"

            if (taskName.isEmpty()) {
                Toast.makeText(this, "Task Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (taskDate.isEmpty()) {
                Toast.makeText(this, "Task Date cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (taskDescription.isEmpty()) {
                Toast.makeText(this, "Task Description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var model= TaskModel(
                "",
                taskName,
                taskDate,
                taskDescription,
                TaskPriority = selectedPriority,
                TaskCategory = selectedCategory)

            taskViewModel.addTask(model){
                    success,message->
                if(success){
                    Toast.makeText(this@AddTaskActivity,
                        message, Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@AddTaskActivity,
                        message, Toast.LENGTH_LONG).show()
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