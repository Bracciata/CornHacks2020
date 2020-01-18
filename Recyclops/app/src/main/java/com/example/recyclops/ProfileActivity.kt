package com.example.recyclops


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class ProfileActivity : AppCompatActivity() {

    // TODO: Add back button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateProfile()
    }
    private fun populateProfile() {
        setContentView(R.layout.activity_profile)
        // Populate profile
    }
    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
}