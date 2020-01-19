package com.example.recyclops


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson

class ProfileActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateProfile()
    }

    private fun populateProfile() {
        setContentView(R.layout.activity_profile)
        // Add a back button to the toolbar.
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Populate profile
        // Get the user that is signed in.
        val activeUser: User = getSignedInUser()
        // Using the signed in user display public information about them.
        populateUserInformation(activeUser)
        // Display a list of rewards they previously redeemed.
        populateRewardsRedemptionHistory(activeUser)
        // Add a click listener to the log out button.
        val logOutButton = findViewById<Button>(R.id.logoutButton)
        logOutButton.setOnClickListener {
            logout()
        }
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

    @SuppressLint("SetTextI18n")
    private fun populateUserInformation(activeUser: User) {
        // Take information from the user and populate it in text views.
        findViewById<TextView>(R.id.nameText).text =
            "Hello, ${activeUser.firstName} ${activeUser.lastName}."
        findViewById<TextView>(R.id.emailText).text = "Email: ${activeUser.email}"
        findViewById<TextView>(R.id.currentPointsText).text =
            "You currently have ${activeUser.points} points."
        findViewById<TextView>(R.id.totalPointsText).text =
            "You have earned ${activeUser.totalPoints} points in total."
    }

    private fun getSignedInUser(): User {
        // Find the user that is actively signed in and return their object.
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val userJson = sharedPreferences.getString("active_user_key", "{}")
        return Gson().fromJson(userJson, User::class.java)
    }

    private fun logout() {
        // Reset signed in user and open main.
        // Wipe the currently logged in user from shared preferences.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val emptyUser = "{}"
        editor.putString("active_user_key", emptyUser)
        editor.commit()
        // Notify the user via toast that they signed out.
        Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show()
        // Return the user to the main camera screen.
        returnToMain()
    }


    private fun returnToMain() {
        // Reopen main.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun populateRewardsRedemptionHistory(activeUser: User) {
        // Populate items they have previously redeemed slash purchased with points.
        // Also display information about the purchase such as cost of the item at that time
        // and when they purchased it.
        val layout = findViewById<ConstraintLayout>(R.id.history_layout)
        val listView = ListView(this)
        val redemptions = activeUser.redemptionHistory
        // Create a list of strings with each one representing a prior purchase.
        val redemptionStrings = mutableListOf<String>()
        for (redemption in redemptions) {
            redemptionStrings.add("Bought ${redemption.rewardRedeemed.title} for ${redemption.purchaseCost} on ${redemption.redemptionTime}")
        }
        // Display the strings in the list view.
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, redemptionStrings)
        listView.adapter = adapter
        // Add the list view to a layout.
        layout.addView(listView)
    }
}