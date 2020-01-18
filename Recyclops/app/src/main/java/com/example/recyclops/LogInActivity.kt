package com.example.recyclops

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
        val sign_in_button = findViewById(R.id.signInButton) as Button
        // set on-click listener
        sign_in_button.setOnClickListener {
            attemptSignIn()

        }
        val register_button = findViewById(R.id.registerButton) as Button
        // set on-click listener
        register_button.setOnClickListener {
            openRegister()

        }
    }
    private fun attemptSignIn(){

    }
    private fun openRegister(){

    }
}