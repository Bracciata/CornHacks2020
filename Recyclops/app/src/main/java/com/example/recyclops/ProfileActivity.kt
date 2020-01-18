package com.example.recyclops


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
            populateRequestList(activeUser)

            populateFriendsList(activeUser)
        val add_friend_button = findViewById(R.id.addFriendButton) as Button
        // Add on click listener to open camera screen.
        add_friend_button.setOnClickListener {
            addFriend(activeUser.getId())
        }
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
            findPersonWithId(friendId, listOfUsers)
        }else{
            // They tried to add themselves as a friend. Sad.
        }
    }
    private fun populateRequestList(activeUser: User){

    }
    private fun findPersonWithId(id:String){
        // Try to find user from list of users
        // TODO: add search through users
        // TODO: add toast as to if user was found
        // If found say sent friend request
        // If not found say user with that id could not be found
    }
    private fun acceptFriendRequest(){

    }
    private fun rejectFriendRequests(){

    }
}