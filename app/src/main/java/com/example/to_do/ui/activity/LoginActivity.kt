package com.example.to_do.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.to_do.R
import com.example.to_do.databinding.ActivityLoginBinding
import com.example.to_do.repository.UserRepositoryImpl
import com.example.to_do.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkBoxPass.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show password
                binding.editPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                // Hide password
                binding.editPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }


        binding.btnSignupnavigate.setOnClickListener {
            val intent = Intent(
                this@LoginActivity, SignupActivity::class.java
            )
            startActivity(intent)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()

            when {
                email.isEmpty() && password.isEmpty() -> {
                    showToast("Both email and password must be filled")
                }
                email.isEmpty() -> {
                    showToast("Please enter your email")
                }
                password.isEmpty() -> {
                    showToast("Please enter your password")
                }
                else -> {
                    userViewModel.login(email, password) { success, message ->
                        if (success) {
                            showToast("Login Successful")
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            handleFirebaseError(message)
                        }
                    }
                }
            }
        }
    }


    private fun handleFirebaseError(errorMessage: String) {
        when {
            errorMessage.contains("INVALID_PASSWORD", true) ||
                    errorMessage.contains("Password is invalid", true) ||
                    errorMessage.contains("The supplied auth credential is incorrect", true) -> {
                showToast("Incorrect password. Please try again.")
            }
            else -> {
                showToast(errorMessage)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
    }



}


