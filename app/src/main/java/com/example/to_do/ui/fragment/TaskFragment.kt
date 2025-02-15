package com.example.to_do.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do.adapter.TaskAdapter
import com.example.to_do.databinding.FragmentTaskBinding
import com.example.to_do.repository.TaskRepositoryImpl
import com.example.to_do.ui.activity.AddTaskActivity
import com.example.to_do.viewmodel.TaskViewModel



class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        taskViewModel.getAllTasks() // Fetch latest tasks
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel and Adapter
        val repo = TaskRepositoryImpl()
        taskViewModel = TaskViewModel(repo)
        adapter = TaskAdapter(requireContext(), ArrayList())

        // Set up RecyclerView
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe tasks from ViewModel
        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let {
                adapter.updateData(it)
            }
        }

        // Observe loading state
        taskViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }


        // Fetch all tasks
        taskViewModel.getAllTasks()

        // Set up FAB to navigate to AddTaskActivity
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivity(intent)
        }



        // Set up swipe to delete functionality
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // We don't support moving items
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = adapter.getTaskID(viewHolder.adapterPosition)

                if (direction == ItemTouchHelper.RIGHT) {
                    // Swipe Right: Delete Task
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setNegativeButton("No") { dialog, _ ->
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            dialog.dismiss()
                        }
                        .setPositiveButton("Yes") { dialog, _ ->
                            taskViewModel.deleteTask(id) { success, message ->
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Swipe Left: Move Task to CompletedFragment
                    AlertDialog.Builder(requireContext())
                        .setTitle("Mark as Completed?")
                        .setMessage("Do you want to move this task to completed?")
                        .setNegativeButton("No") { dialog, _ ->
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            dialog.dismiss()
                        }
                        .setPositiveButton("Yes") { dialog, _ ->
                            taskViewModel.moveTask(id) { success, message ->
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }).attachToRecyclerView(binding.recyclerView)
    }
}