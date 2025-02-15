package com.example.to_do.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.to_do.databinding.FragmentProfileBinding
import com.example.to_do.repository.TaskRepositoryImpl
import com.example.to_do.repository.UserRepositoryImpl
import com.example.to_do.ui.activity.LoginActivity
import com.example.to_do.utils.ImageUtils
import com.example.to_do.utils.LoadingUtils
import com.example.to_do.viewmodel.TaskViewModel
import com.example.to_do.viewmodel.TaskViewModelFactory
import com.example.to_do.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {



    private lateinit var binding: FragmentProfileBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var database: FirebaseDatabase
    private lateinit var taskRef: DatabaseReference
    private lateinit var userViewModel: UserViewModel
    private lateinit var completedTaskRef: DatabaseReference
    lateinit var loadingUtils: LoadingUtils
    lateinit var imageUtils: ImageUtils
    var imageUri: Uri? = null


        
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase references
        database = FirebaseDatabase.getInstance()
        taskRef = database.getReference("tasks")  // Incomplete tasks
        completedTaskRef = database.getReference("completedTasks")  // Completed tasks

        database = FirebaseDatabase.getInstance()
        // Initialize ViewModel
        val repo = TaskRepositoryImpl()
        val factory = TaskViewModelFactory(repo)
        taskViewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)


        // Fetch task counts and update UI
        fetchTaskCounts()
        fetchProfileImage()


        // Set up button listeners
        binding.buttonReset.setOnClickListener { confirmDeleteAllTasks() }
        binding.buttonLogout.setOnClickListener { logoutUser() }

        binding.editProfileButton.setOnClickListener{uploadImage()}

        imageUtils = ImageUtils(this)

        loadingUtils = LoadingUtils(this)

        var repo1 = UserRepositoryImpl()
        userViewModel = UserViewModel(repo1)

        imageUtils.registerActivity { url ->
            url.let { it ->
                showConfirmationDialog {
                    // If the user clicks Yes, upload the image
                    imageUri = it
                    uploadImage() // Call the upload image function
                }
            }
        }
        binding.editProfileButton.setOnClickListener {
            imageUtils.launchGallery(this)
        }


    }

    private fun showConfirmationDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Upload")
            .setMessage("Do you want to upload this image?")
            .setPositiveButton("Yes") { dialog, _ ->
                onConfirm() // Call the uploadImage function if the user clicks Yes
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Close the dialog if No is clicked
            }
            .setCancelable(false)
            .show()
    }


    private fun fetchProfileImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId ?: "")

        userRef.child("profileImage").get().addOnSuccessListener { snapshot ->
            val imageUrl = snapshot.value as? String
            if (imageUrl != null) {
                Picasso.get().load(imageUrl).into(binding.profileImage)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

private fun fetchTaskCounts() {
        var incompleteCount = 0
        var completedCount = 0
        var tasksFetched = 0

        println("Fetching task counts from Firebase...")

        // Fetch incomplete task count
        taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                incompleteCount = snapshot.childrenCount.toInt()
                println("Incomplete Tasks: $incompleteCount") // Debug log
                tasksFetched++
                if (tasksFetched == 2) updateUI(incompleteCount, completedCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load tasks", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch completed task count
        completedTaskRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completedCount = snapshot.childrenCount.toInt()
                println("Completed Tasks: $completedCount") // Debug log
                tasksFetched++
                if (tasksFetched == 2) updateUI(incompleteCount, completedCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load completed tasks", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(incompleteCount: Int, completedCount: Int) {
        requireActivity().runOnUiThread {
            binding.completedTaskCount.text = "Completed Tasks: $completedCount"
            binding.incompleteTaskCount.text = "Incomplete Tasks: $incompleteCount"

            val totalTasks = incompleteCount + completedCount
            val progress = if (totalTasks > 0) (completedCount * 100) / totalTasks else 0

            println("Updating UI: Progress = $progress%") // Debugging
            binding.taskProgressIndicator.setProgress(progress, true)
            binding.taskProgressIndicator.visibility = View.VISIBLE  // Ensure it's visible

            binding.taskProgressText.text = "Completion: $progress%"
        }
    }

    private fun confirmDeleteAllTasks() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All Tasks")
            .setMessage("Are you sure you want to delete all tasks? This action cannot be undone.")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteAllTasks()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun deleteAllTasks() {
        taskViewModel.deleteAllTasks { success, message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            fetchTaskCounts() // Refresh counts after deletion
        }
    }
    private fun logoutUser() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to log out?")
            .setCancelable(false) // Prevents dismissal by tapping outside the dialog
            .setPositiveButton("Yes") { _, _ ->
                // Proceed with the logout
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                requireActivity().supportFragmentManager.popBackStackImmediate()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Just dismiss the dialog
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }



    private fun uploadImage() {
        loadingUtils.show()
        imageUri?.let { uri ->
            userViewModel.uploadImage(this, uri) { imageUrl ->
                loadingUtils.dismiss()
                if (imageUrl != null) {
                    // Save the image URL to Firebase or your database
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId ?: "")
                    userRef.child("profileImage").setValue(imageUrl) // Store the image URL in Firebase

                    // Update the ImageView
                    Picasso.get().load(imageUrl).into(binding.profileImage)

                    Log.d("Upload Success", "Image uploaded: $imageUrl")
                } else {
                    Log.e("Upload Error", "Failed to upload image")
                }
            }
        } ?: run {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }





}


