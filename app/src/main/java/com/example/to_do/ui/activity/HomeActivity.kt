package com.example.to_do.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.to_do.R
import com.example.to_do.databinding.ActivityHomeBinding
import com.example.to_do.databinding.ActivityLoginBinding
import com.example.to_do.ui.fragment.CompletedFragment
import com.example.to_do.ui.fragment.ProfileFragment
import com.example.to_do.ui.fragment.TaskFragment

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(TaskFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeId -> replaceFragment(TaskFragment())
                R.id.profileId -> replaceFragment(ProfileFragment())
                R.id.completeId-> replaceFragment(CompletedFragment())
                else -> {

                }
            }
            true
        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentransaction: FragmentTransaction =fragmentManager.beginTransaction()
        fragmentransaction.replace(binding.frameLayout.id,fragment)
        fragmentransaction.commit()

    }
}