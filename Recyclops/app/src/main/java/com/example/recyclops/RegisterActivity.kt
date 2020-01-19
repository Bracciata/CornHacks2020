package com.example.recyclops

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


class RegisterActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRegistration()
    }

    private fun setupRegistration() {
        setContentView(R.layout.activity_register)
        // Add listener for register button being clicked.
        val registerButton = findViewById<Button>(R.id.complete_registration_button)
        registerButton.setOnClickListener {
            register()
        }
        // Add a back button to the toolbar.
        var toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

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
        // Reopen the main menu.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun register() {
        // Get the entered information and compare against list of existing users to determine
        // if the new account is valid.
        // Get the list of existing users.
        var listOfUsers = getUsers()
        // Get the entered information.
        var emailEditText = findViewById<EditText>(R.id.register_email_edit_text)
        var passwordEditText = findViewById<EditText>(R.id.register_password_edit_text)
        var fNameEditText = findViewById<EditText>(R.id.register_first_name_edit_text)
        var lNameEditText = findViewById<EditText>(R.id.register_last_name_edit_text)
        // Check that there is not an existing user with the same email.s
        for (user in listOfUsers) {
            if (user.email == emailEditText.text.toString()) {
                Toast.makeText(
                    this,
                    "Sorry ${emailEditText.text} is already taken as an email",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }
        // They passed the comparison so create the user.
        var newUser = User(
            fNameEditText.text.toString(),
            lNameEditText.text.toString(),
            emailEditText.text.toString(),
            passwordEditText.text.toString(),
            listOfUsers.size.toString()
        )
        // Add the user to list of users.
        listOfUsers.add(newUser)
        // Set the signed in user to the user that was just created.
        setSignedInUser(newUser)
        // Update the entire list of users to save the new user.
        updateListOfUsers(listOfUsers)
        // Open Profile page
        openProfile()
    }

    private fun updateListOfUsers(listOfUsers: List<User>) {
        // Update the list of users in shared preferences.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(listOfUsers)
        editor.putString("users_key", usersJson)
        editor.commit()
    }

    private fun getUsers(): MutableList<User> {
        // Get the list of all users from shared preferences.
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val userJson = sharedPreferences.getString("users_key", "{}")
        return Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
    }

    private fun setSignedInUser(userFound: User) {
        // Assign the recently created user to be considered signed in by shared preferences.
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(userFound)
        editor.putString("active_user_key", usersJson)
        editor.commit()
    }

    private fun openProfile() {
        // Open the profile after the user was created.
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}