package com.example.recyclops

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson

class LeaderboardsAndFriendsActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateSocial()
    }

    private fun populateSocial() {
        setContentView(R.layout.activity_leaderboard_friends)
        val activeUser = getSignedInUser()
        populateRequestList(activeUser)
        populateLeaderboard(activeUser)
        val toolbar : Toolbar = findViewById(R.id.toolbar)
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

    private fun populateLeaderboard(activeUser: User){
        val friends = activeUser.friends
        Log.e("HERE",friends[0].firstName)
        friends.sortedBy { friend -> friend.totalPoints }
        friends.reverse()

        // Populate in list view.
        val stringsForLeaderboard: MutableList<String> = mutableListOf()
        var count = 1
        for(friend in friends){
            stringsForLeaderboard.add("${count}. ${friend.firstName} ${friend.lastName}(Total points: ${friend.totalPoints})")
            count += 1
        }
        val layout = findViewById<RelativeLayout>(R.id.leaderboard_layout)
        val listView = ListView(this)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            stringsForLeaderboard)
        layout.addView(listView)
        listView.setOnItemClickListener { _, _, position, _ ->
            val friendToFocus = friends[position]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Remove Friend?")
            builder.setMessage("Would you like to remove ${friendToFocus.firstName} ${friendToFocus.lastName}?")
            builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
                        activeUser.friends.remove(friendToFocus)
                        Toast.makeText(
                            applicationContext,
                            "Removed ${friendToFocus.firstName} ${friendToFocus.lastName}!", Toast.LENGTH_SHORT
                        ).show()
                // Update list of users and save.
                saveSignedOnUser(activeUser)
                saveListOfUsers(activeUser)
                dialog.dismiss()
                // Reload leader board and friends
                reload()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Cancelled", Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            builder.show()
        }
    }
    private fun reload(){
        val intent = Intent(this, LeaderboardsAndFriendsActivity::class.java)
        // start your next activity
        startActivity(intent)
    }

    private fun getUsers():List<User>{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("users_key","{}")
        return Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
    }

    private fun getSignedInUser(): User{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("active_user_key","{}")
        return Gson().fromJson(userJson, User::class.java)
    }

    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }

    @SuppressLint("ShowToast")
    private fun addFriend(userId:String, listOfUsers:List<User>){
        val friendIdEditText = findViewById<EditText>(R.id.friendId)
        val friendId = friendIdEditText.text.toString()
        if(userId!==friendId) {
            for(user in listOfUsers){
                if(user.getId()==friendId){
                    user.addRequest(userId)
                    Toast.makeText(this,"Sent ${user.firstName} a friend request.",Toast.LENGTH_LONG)
                    // Save users to add request
                    updateUsers(listOfUsers)
                    return
                }
            }
        }
        Toast.makeText(this,"Could not find a user with the id: $friendId.",Toast.LENGTH_LONG)
    }

    private fun updateUsers(users: List<User>){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(users)
        editor.putString("users_key", usersJson)
        editor.apply()
    }

    private fun populateRequestList(activeUser: User){
        val layout = findViewById<RelativeLayout>(R.id.requestLayout)
        val listView = ListView(this)
        val requestStrings: MutableList<String> = mutableListOf()
        for(request in activeUser.friendRequestsIncomingUserIds){
            requestStrings.add("Click to accept or reject the user with the id $request")
        }
        val users = getUsers()
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
        requestStrings)
        listView.setOnItemClickListener { _, _, position, _ ->

            val requestToFocus = activeUser.friendRequestsIncomingUserIds[position]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add friend?")
            builder.setMessage("Would you like to add the user with the ID: ${requestToFocus}?")
            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        for(user in users) {
                            if(user.getId()==requestToFocus) {
                                activeUser.addFriend(user)
                                activeUser.friendRequestsIncomingUserIds.removeAt(position)
                                Toast.makeText(
                                    applicationContext,
                                    "Added ${user.firstName} ${user.lastName} as friend!", Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        // Update list of users and save.
                        saveSignedOnUser(activeUser)
                        saveListOfUsers(activeUser)
                reload()

                dialog.dismiss()

            }

            builder.setNegativeButton(android.R.string.no) { dialog, _ ->
                activeUser.friendRequestsIncomingUserIds.removeAt(position)
                Toast.makeText(
                        applicationContext,
                "Removed request from $requestToFocus", Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                reload()

            }
            builder.setNeutralButton("Cancel") { dialog, _ ->

                dialog.dismiss()

            }

            builder.show()

        }
        layout.addView(listView)
    }
    private fun saveSignedOnUser(userActive:User){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(userActive)
        editor.putString("active_user_key",usersJson)
        editor.apply()
    }
    private fun saveListOfUsers(currentUser:User){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("users_key","[]")
        val userList:  MutableList<User> = Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
        for (user in userList){
            if(user.email == currentUser.email){
                user.friends = currentUser.friends
                user.friendRequestsIncomingUserIds = currentUser.friendRequestsIncomingUserIds
            }
        }
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(userList)
        editor.putString("users_key",usersJson)
        editor.apply()
    }

}