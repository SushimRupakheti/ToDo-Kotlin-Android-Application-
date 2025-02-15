package com.example.to_do.ui.fragment

import android.app.AlertDialog
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
import com.example.to_do.databinding.FragmentCompletedBinding
import com.example.to_do.repository.TaskRepositoryImpl
import com.example.to_do.viewmodel.TaskViewModel


class CompletedFragment : Fragment() {

    private lateinit var binding: FragmentCompletedBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompletedBinding.inflate(inflater, container, false)

        // Initialize ViewModel with repository
        val repo = TaskRepositoryImpl()
        taskViewModel = TaskViewModel(repo)

        adapter = TaskAdapter(requireContext(), ArrayList())
        binding.recyclerViewCompleted.adapter = adapter
        binding.recyclerViewCompleted.layoutManager = LinearLayoutManager(requireContext())

        // Observe LiveData for completed tasks
        taskViewModel.completedTasks.observe(viewLifecycleOwner) { tasks ->
            if (tasks.isNullOrEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.recyclerViewCompleted.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.recyclerViewCompleted.visibility = View.VISIBLE
                adapter.updateData(tasks)
            }
        }


        taskViewModel.getCompletedTasks() // Fetch completed tasks

        // Set up swipe functionality
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // We don't support moving items manually
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
                            taskViewModel.deleteCompTask(id) { success, message ->
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Swipe Left: Move Task Back to TaskFragment
                    AlertDialog.Builder(requireContext())
                        .setTitle("Move to Pending Tasks?")
                        .setMessage("Do you want to move this task back to pending tasks?")
                        .setNegativeButton("No") { dialog, _ ->
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            dialog.dismiss()
                        }
                        .setPositiveButton("Yes") { dialog, _ ->
                            taskViewModel.moveCompTask(id) { success, message ->
                                if (success) {
                                    Toast.makeText(requireContext(), "Task moved to pending", Toast.LENGTH_SHORT).show()
                                    taskViewModel.getAllTasks() // Refresh task list in TaskFragment
                                    taskViewModel.getCompletedTasks() // Refresh completed tasks
                                } else {
                                    Toast.makeText(requireContext(), "Failed to move task", Toast.LENGTH_SHORT).show()
                                }
                            }
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        }).attachToRecyclerView(binding.recyclerViewCompleted)



        return binding.root


    }
}
