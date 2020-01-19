package com.example.recyclops

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
        var toolbar: Toolbar = findViewById(R.id.toolbarRewards)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val constraintLayout = findViewById<RelativeLayout>(R.id.relative_layout_rewards_list)
        val listView = ListView(this)
        val rewards = getRewards()
        val rewardStrings = mutableListOf<String>()
        for (reward in rewards) {
            rewardStrings.add("Buy ${reward.title} for ${reward.saleCost} points")
        }

        listView.adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            rewardStrings
        )
        listView.setOnItemClickListener { _, _, position, _ ->

            val rewardToFocus = rewards[position]
            val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
            builder.setTitle("Purchase?")
            builder.setMessage("Would you like to purchase ${rewardToFocus.title} for ${rewardToFocus.saleCost} points?")
            builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
                var user = getSignedInUser()
                if (user !== null) {
                    if (user.points >= rewardToFocus.saleCost) {

                        Toast.makeText(
                            applicationContext,
                            "Purchased ${rewardToFocus.title}", Toast.LENGTH_SHORT
                        ).show()
                        user.redeemPrize(rewardToFocus)
                        // Update list of users and save.
                        saveSignedOnUser(user)
                        saveListOfUsers(user)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "You do not have enough points for ${rewardToFocus.title}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "You need to log in first!", Toast.LENGTH_SHORT
                    ).show()

                }
                dialog.dismiss()

            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.dismiss()

            }

            builder.show()

        }
        constraintLayout.addView(listView)

    }

    private fun saveSignedOnUser(userActive: User) {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(userActive)
        editor.putString("active_user_key", usersJson)
        editor.commit()
    }

    private fun saveListOfUsers(currentUser: User) {
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

    fun getSignedInUser(): User {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("active_user_key", "{}")
        return Gson().fromJson(userJson, User::class.java)
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

    private fun getRewards(): MutableList<Reward> {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val rewardsJson = sharedPreferences.getString("rewards_key", "{}")
        return Gson().fromJson(rewardsJson, Array<Reward>::class.java).toMutableList()
    }
}