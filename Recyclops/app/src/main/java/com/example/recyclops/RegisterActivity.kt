package com.example.recyclops

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRegistration()
    }
    private fun setupRegistration() {
        setContentView(R.layout.activity_register)
        // TODO: Add onclick listener to button on register page

    }
    private fun register(){
        // Save account
        // TODO: Add save account
        // Open Profile page
        openProfile()
    }
    private fun openProfile(){
        val intent = Intent(this, ProfileActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
}