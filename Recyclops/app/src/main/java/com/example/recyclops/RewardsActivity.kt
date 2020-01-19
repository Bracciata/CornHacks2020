package com.example.recyclops

import android.content.Context
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

        val constraintLayout = findViewById(R.id.relativeLayoutRewards) as RelativeLayout
        val listView = ListView(this)
        val rewards = getRewards()
        val rewardStrings = mutableListOf<String>()
        for (reward in rewards) {
            rewardStrings.add("Buy ${reward.title} for ${reward.cost} on ${reward.timeAdded}")
        }

            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
                rewardStrings) as ListAdapter?
        listView.setOnItemClickListener { parent, view, position, id ->

           val rewardToFocus = rewards[position]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Purchase?")
            builder.setMessage("Would you like to purchase ${rewardToFocus.title} for ${rewardToFocus.saleCost}?")
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                var user = getSignedInUser()
                if (user !== null) {
                    if(user.points >= rewardToFocus.saleCost) {

                        Toast.makeText(
                            applicationContext,
                            "Purchased ${rewardToFocus.title}", Toast.LENGTH_SHORT
                        ).show()
                        user.redeemPrize(rewardToFocus)
                        // Update list of users and save.
                        saveSignedOnUser(user)
                        saveListOfUsers(user)
                    }else{
                        Toast.makeText(
                            applicationContext,
                            "You do not have enough points for ${rewardToFocus.title}", Toast.LENGTH_LONG
                        ).show()
                    }
                }else{
                    Toast.makeText(applicationContext,
                        "You need to log in first!", Toast.LENGTH_SHORT).show()

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
    fun saveSignedOnUser(userActive:User){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(userActive)
        editor.putString("active_user_key",usersJson)
        editor.commit()
    }
    fun saveListOfUsers(currentUser:User){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("users_key","[]")
        val userList:  MutableList<User> = Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
        for (user in userList){
            if(user.email == currentUser.email){
                user.redemptionHistory = currentUser.redemptionHistory
                user.points = currentUser.points
            }
        }
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(userList)
        editor.putString("users_key",usersJson)
        editor.commit()
    }
    fun getSignedInUser(): User{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("active_user_key","{}")
        val user :  User = Gson().fromJson(userJson, User::class.java)
        return user
    }

    fun getRewards(): MutableList<Reward> {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val rewardsJson = sharedPreferences.getString("rewards_key", "{}")
        val rewardList: MutableList<Reward> = Gson().fromJson(rewardsJson, Array<Reward>::class.java).toMutableList()
        return rewardList
    }
}