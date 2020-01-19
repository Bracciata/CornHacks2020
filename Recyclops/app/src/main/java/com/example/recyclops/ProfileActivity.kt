package com.example.recyclops


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
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
        // Populate profile
        // Get the id of the signed in user
        val activeUser:User = getSignedInUser()
        populateUserInformation(activeUser)
        populateRewardsRedemptionHistory(activeUser)
    }
    private fun populateUserInformation(activeUser: User){

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
        returnToMain()

    }


    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
   private fun populateRewardsRedemptionHistory(activeUser:User){
       var listView = findViewById<ListView>(R.id.redemption_history_list_view)

       val redemptions = activeUser.redemptionHistory
       val redemptionStrings = mutableListOf<String>()
       for(redemption in redemptions){
           redemptionStrings.add("Bought ${redemption.rewardRedeemed.title} for ${redemption.purchaseCost} on ${redemption.redemptionTime}")
       }
       val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, redemptionStrings)
       listView.adapter = adapter

    }
}