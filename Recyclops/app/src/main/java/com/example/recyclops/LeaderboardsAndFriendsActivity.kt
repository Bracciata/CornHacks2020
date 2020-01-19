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

class LeaderboardsAndFriendsActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateSocial()
    }
    private fun populateSocial() {
        setContentView(R.layout.activity_leaderboard_friends)
        var activeUser = getSignedInUser()
        populateRequestList(activeUser)
        populateFriendsList(activeUser)
        populateLeaderboard(activeUser)
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
    private fun populateLeaderboard(activeUser: User){
        var friends = activeUser.friends
        friends.sortedBy { friend -> friend.totalPoints }
        // Populate in list view.
        var stringsForLeaderboard: MutableList<String> = mutableListOf()
        var count: Int = 1
        for(friend in friends){
            stringsForLeaderboard.add("${count}. ${friend.firstName} ${friend.lastName}(Total points: ${friend.totalPoints}")
        }
        val layout = findViewById(R.id.requestLayout) as RelativeLayout
        val listView = ListView(this)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            stringsForLeaderboard) as ListAdapter?
        layout.addView(listView)
    }
    fun getUsers():List<User>{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("users_key","{}")
        val userList:  MutableList<User> = Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
        return userList
    }
    fun getSignedInUser(): User{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("active_user_key","{}")
        val user :  User = Gson().fromJson(userJson, User::class.java)
        return user
    }

    private fun populateFriendsList(activeUser: User){

    }
    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
    private fun addFriend(userId:String, listOfUsers:List<User>){
        val friendIdEditText = findViewById(R.id.friendId) as EditText
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
        }else{
            // They tried to add themselves as a friend. Sad.
        }
        Toast.makeText(this,"Could not find a user with the id: $friendId.",Toast.LENGTH_LONG)

    }
    private fun updateUsers(users: List<User>){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(users)
        editor.putString("users_key", usersJson)
        editor.commit()
    }

    private fun populateRequestList(activeUser: User){
        val layout = findViewById(R.id.requestLayout) as RelativeLayout
        val listView = ListView(this)
        var requestStrings: MutableList<String> = mutableListOf()
        for(request in activeUser.friendRequestsIncomingUserIds){
            requestStrings.add("Click to accept or reject the user with the id ${request}")
        }
        var users = getUsers()
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
        requestStrings) as ListAdapter?
        listView.setOnItemClickListener { parent, view, position, id ->

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

                dialog.dismiss()

            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                activeUser.friendRequestsIncomingUserIds.removeAt(position)
                Toast.makeText(
                        applicationContext,
                "Removed request from ${requestToFocus}", Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
            builder.setNeutralButton("Cancel") { dialog, which ->

                dialog.dismiss()

            }

            builder.show()

        }
        layout.addView(listView)
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
                user.friends = currentUser.friends
                user.friendRequestsIncomingUserIds = currentUser.friendRequestsIncomingUserIds
            }
        }
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(userList)
        editor.putString("users_key",usersJson)
        editor.commit()
    }

}