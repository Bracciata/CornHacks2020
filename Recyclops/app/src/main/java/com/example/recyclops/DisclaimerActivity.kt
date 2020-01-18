package com.example.recyclops

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class DisclaimerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Begin with Pokemon Go esque disclaimer on recycling
        openDisclaimer()
    }

    private fun openDisclaimer() {
        // Begin with Pokemon Go esque disclaimer on recycling
        setContentView(R.layout.activity_main_disclaimer)
        val begin_button = findViewById(R.id.openCameraButton) as Button
        // Add on click listener to open camera screen.
        begin_button.setOnClickListener {
            openMain()
        }
    }

    private fun openMain() {
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
}