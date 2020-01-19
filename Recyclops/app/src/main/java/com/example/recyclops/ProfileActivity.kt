package com.example.recyclops


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson

class ProfileActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    // TODO: Add back button
    // TODO: add log out button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateProfile()
    }
    private fun populateProfile() {
        setContentView(R.layout.activity_profile)
        var toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Populate profile
        // Get the id of the signed in user
        val activeUser:User = getSignedInUser()
        populateUserInformation(activeUser)
        populateRewardsRedemptionHistory(activeUser)
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
    private fun populateUserInformation(activeUser: User){
        findViewById<TextView>(R.id.nameText).text = "Hello, ${activeUser.firstName} ${activeUser.lastName}."
        findViewById<TextView>(R.id.emailText).text = "Email: ${activeUser.email}"
        findViewById<TextView>(R.id.currentPointsText).text = "You currently have ${activeUser.points} points."
        findViewById<TextView>(R.id.totalPointsText).text = "You have earned ${activeUser.totalPoints} points in total."
    }
    fun getSignedInUser(): User{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("active_user_key","{}")
        val user :  User = Gson().fromJson(userJson, User::class.java)
        return user
    }

    fun logout(){
        // Reset signed in user and open main.
            val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
            val emptyUser = "{}"
            editor.putString("active_user_key", emptyUser)
            editor.commit()
        returnToMain()

    }


    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
   private fun populateRewardsRedemptionHistory(activeUser:User){
       var layout = findViewById<ConstraintLayout>(R.id.history_layout)
       val listView = ListView(this)
       val redemptions = activeUser.redemptionHistory
       val redemptionStrings = mutableListOf<String>()
       for(redemption in redemptions){
           redemptionStrings.add("Bought ${redemption.rewardRedeemed.title} for ${redemption.purchaseCost} on ${redemption.redemptionTime}")
       }
       val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, redemptionStrings)
       listView.adapter = adapter
       layout.addView(listView)

    }
}