package com.example.recyclops

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
        val registerButton = findViewById(R.id.completeRegistrationButton) as Button
        // set on-click listener
        registerButton.setOnClickListener {
            register()
        }
        var toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            // Open Camera
            returnToMain()
            true
        }else ->{
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
    private fun register(){
        // Save account
        var listOfUsers = getUsers()
        var emailEditText = findViewById<EditText>(R.id.registerEmailEditText)
        var passwordEditText = findViewById<EditText>(R.id.registerPasswordEditText)
        var fNameEditText = findViewById<EditText>(R.id.registerFirstNameEditText)
        var lNameEditText = findViewById<EditText>(R.id.registerLastNameEditText)
        for(user in listOfUsers){
            if(user.email==emailEditText.text.toString()){
                Toast.makeText(this, "Sorry ${emailEditText.text} is already taken as an email", Toast.LENGTH_LONG).show()
                return
            }
        }
        var newUser = User(fNameEditText.text.toString(),lNameEditText.text.toString(),emailEditText.text.toString(),passwordEditText.text.toString(),listOfUsers.size.toString())
        listOfUsers.add(newUser)
        setSignedInUser(newUser)
        updateListOfUsers(listOfUsers)
        // Open Profile page
        openProfile()
    }
private fun updateListOfUsers(listOfUsers:List<User>){
    val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
    val usersJson = Gson().toJson(listOfUsers)
    editor.putString("users_key",usersJson)
    editor.commit()
}
private fun getUsers():MutableList<User>{
    val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
        Context.MODE_PRIVATE)
    val userJson = sharedPreferences.getString("users_key","{}")
    val userList:  MutableList<User> = Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
    return userList
}
private fun setSignedInUser(userFound: User){
    val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
        Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
    val usersJson = Gson().toJson(userFound)
    editor.putString("active_user_key",usersJson)
    editor.commit()
}
    private fun openProfile(){
        val intent = Intent(this, ProfileActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
}