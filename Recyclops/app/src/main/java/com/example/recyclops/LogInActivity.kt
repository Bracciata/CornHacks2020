package com.example.recyclops

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class LogInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLogIn()
    }
    private fun setupLogIn(){
        setContentView(R.layout.activity_log_in)
        // get reference to button
        val signInButton = findViewById(R.id.signInButton) as Button
        // set on-click listener
        signInButton.setOnClickListener {
            attemptSignIn()

        }
        val registerButton = findViewById(R.id.registerButton) as Button
        // set on-click listener
        registerButton.setOnClickListener {
            openRegister()

        }
    }
    private fun attemptSignIn(){
        //TODO: Add signing in verification.
    }
    private fun openProfile(){
        // Upon success signing in in open the profile.
        val intent = Intent(this, ProfileActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
    private fun openRegister(){
        val intent = Intent(this, RegisterActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
}