package com.example.recyclops

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson


class LogInActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupLogIn()
    }

    private fun setupLogIn() {
        setContentView(R.layout.activity_log_in)
        // Get the list of all users
        // Note the following is temporary as later on it will be based off of an online database.
        val listOfUsers: List<User> = getUsers()
        // get reference to button
        val signInButton = findViewById<Button>(R.id.signInButton)
        // set on-click listener
        signInButton.setOnClickListener {
            attemptSignIn(listOfUsers)
        }
        val registerButton = findViewById<Button>(R.id.registerButton)
        // set on-click listener
        registerButton.setOnClickListener {
            openRegister()
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            // Open Camera
            returnToMain()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun returnToMain() {
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }

    private fun getUsers(): List<User> {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val userJson = sharedPreferences.getString("users_key", "{}")
        return Gson().fromJson(userJson, Array<User>::class.java).toList()
    }

    private fun setSignedInUser(userFound: User) {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(userFound)
        editor.putString("active_user_key", usersJson)
        editor.apply()
    }

    @SuppressLint("DefaultLocale")
    private fun attemptSignIn(listOfUsers: List<User>) {
        val emailEditText = findViewById<EditText>(R.id.userEmailEditText)
        val passwordEditText = findViewById<EditText>(R.id.userPasswordEditText)
        val password = passwordEditText.text.toString()
        val email = emailEditText.text.toString()
        for (user in listOfUsers) {
            if (user.email.toLowerCase() == email.toLowerCase()) {
                // We could also make this end loop here no matter what
                // because there will not be two users with same email
                if (user.checkPassword(password)) {
                    // Create toast that you are signed
                    val firstName = user.firstName
                    Toast.makeText(this, "Hello, $firstName.", Toast.LENGTH_LONG).show()
                    // Open profile
                    setSignedInUser(user)
                    openProfile()
                    return
                }
            }
        }
        // Failed to find correct user
        // Create toast
        Toast.makeText(
            this,
            "Sorry we could not find an account with those credentials. Try again!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun openProfile() {
        // Upon success signing in open the profile.
        val intent = Intent(this, ProfileActivity::class.java)
        // start your next activity
        startActivity(intent)
    }

    private fun openRegister() {
        // Opens the registration screen.
        val intent = Intent(this, RegisterActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
}