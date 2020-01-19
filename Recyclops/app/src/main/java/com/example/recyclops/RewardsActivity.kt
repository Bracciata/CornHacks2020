package com.example.recyclops

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson


class RewardsActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRewards()
    }

    private fun setupRewards() {
        setContentView(R.layout.activity_rewards)
        // Add back button to the toolbar.
        var toolbar: Toolbar = findViewById(R.id.toolbarRewards)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Populate a list view with options for rewards you can redeem.
        val constraintLayout = findViewById<RelativeLayout>(R.id.relative_layout_rewards_list)
        val listView = ListView(this)
        val rewards = getRewards()
        val rewardStrings = mutableListOf<String>()
        // Create a list of strings with each one representing an award.
        for (reward in rewards) {
            rewardStrings.add("Buy ${reward.title} for ${reward.saleCost} points")
        }
        // Add the list of strings as items to the list view.
        listView.adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            rewardStrings
        )
        // Add the option to buy each item when clicked in list view.
        listView.setOnItemClickListener { _, _, position, _ ->
            // Find the reward that was clicked.
            val rewardToFocus = rewards[position]
            // Create a pop up asking if you want to purchase that reward that is clicked.
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Purchase?")
            builder.setMessage("Would you like to purchase ${rewardToFocus.title} for ${rewardToFocus.saleCost} points?")
            builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
                // Get the user and check if they are signed in because they want to purchase.
                var user = getSignedInUser()
                if (user.userIdentification !== "-1") {
                    if (user.points >= rewardToFocus.saleCost) {
                        // Tbe user is signed in so update their purchase history and points.
                        Toast.makeText(
                            applicationContext,
                            "Purchased ${rewardToFocus.title}", Toast.LENGTH_SHORT
                        ).show()
                        user.redeemPrize(rewardToFocus)
                        // Update list of users and save.
                        saveSignedOnUser(user)
                        saveListOfUsers(user)
                    } else {
                        // The user does not have enough points so tell them they can not buy that
                        // reward.
                        Toast.makeText(
                            applicationContext,
                            "You do not have enough points for ${rewardToFocus.title}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Warn the user that they need to log in before they can make a purchase.
                    Toast.makeText(
                        applicationContext,
                        "You need to log in first!", Toast.LENGTH_SHORT
                    ).show()

                }
                dialog.dismiss()

            }

            builder.setNegativeButton(android.R.string.no) { dialog, _ ->
                // They don't want to buy it so simply treat it as a cancel.
                dialog.dismiss()
            }

            builder.show()

        }
        // Display the list view with the reward options.
        constraintLayout.addView(listView)
    }

    private fun saveSignedOnUser(userActive: User) {
        // Save the user after they made a purchase.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(userActive)
        editor.putString("active_user_key", usersJson)
        editor.commit()
    }

    private fun saveListOfUsers(currentUser: User) {
        // Save the user within the list of user with their new post purchase properties.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("users_key", "[]")
        val userList: MutableList<User> =
            Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
        for (user in userList) {
            if (user.email == currentUser.email) {
                user.redemptionHistory = currentUser.redemptionHistory
                user.points = currentUser.points
            }
        }
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(userList)
        editor.putString("users_key", usersJson)
        editor.commit()
    }

    private fun getSignedInUser(): User {
        // Get the user that is signed in if there is any.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("active_user_key", "{}")
        return Gson().fromJson(userJson, User::class.java)
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
        // Reopen main.
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun getRewards(): MutableList<Reward> {
        // Pull the list of rewards so that it can be populated.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val rewardsJson = sharedPreferences.getString("rewards_key", "{}")
        return Gson().fromJson(rewardsJson, Array<Reward>::class.java).toMutableList()
    }
}