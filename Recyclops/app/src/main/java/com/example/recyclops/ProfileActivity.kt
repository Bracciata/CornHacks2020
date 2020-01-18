package com.example.recyclops


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class ProfileActivity : AppCompatActivity() {

    // todo: Add back button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateProfile()
    }
    private fun populateProfile() {
        setContentView(R.layout.activity_profile)
        // Populate profile
    }
}