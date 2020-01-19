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
    }

    private fun populateRequestList(activeUser: User){
        for(request in activeUser.friendRequestsIncomingUserIds){
            
        }
    }
    private fun acceptFriendRequest(){

    }
    private fun rejectFriendRequests(){

    }
}