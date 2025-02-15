package com.example.to_do.ui.activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.to_do.R
import com.example.to_do.databinding.ActivitySignupBinding
import com.example.to_do.model.UserModel
import com.example.to_do.repository.UserRepositoryImpl
import com.example.to_do.viewmodel.UserViewModel

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var repo = UserRepositoryImpl()
        userViewModel = UserViewModel(repo)

        binding.btnLoginNavigate.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.signUp.setOnClickListener {
            var email = binding.registerEmail.text.toString()
            var password = binding.registerPassword.text.toString()
            var firstName = binding.registerFname.text.toString()
            var lastName = binding.registerLName.text.toString()
            var address = binding.registerAddress.text.toString()
            var contact = binding.registerContact.text.toString()

            userViewModel.Signup(email,password){
                    success,message,userId ->
                if(success){
                    var userModel = UserModel(
                        userId,
                        firstName, lastName,
                        address, contact, email
                    )
                    userViewModel.addUserToDatabase(userId,userModel){
                            success,message ->
                        if(success){
                            Toast.makeText(
                                this@SignupActivity,
                                message, Toast.LENGTH_LONG
                            ).show()
                        }else{
                            Toast.makeText(
                                this@SignupActivity,
                                message, Toast.LENGTH_LONG
                            ).show()

                            println(message)
                        }
                    }
                }else{
                    Toast.makeText(
                        this@SignupActivity,
                        message, Toast.LENGTH_LONG
                    ).show()
                }
            }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.submitButton)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}}