package com.example.recyclops


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class ProfileActivity : AppCompatActivity() {

    // TODO: Add back button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateProfile()
    }
    private fun populateProfile() {
        setContentView(R.layout.activity_profile)
        // Populate profile
        val incomingRequestIds = getIncomingFriendRequests()
        if(incomingRequestIds.size>0){
            populateRequestList(incomingRequestIds)
        }
        val friendIds = getListOfFriends()
        if(friendIds.size >0){
            populateFriendsList(friendIds)
        }
        val add_friend_button = findViewById(R.id.addFriendButton) as Button
        // Add on click listener to open camera screen.
        add_friend_button.setOnClickListener {
            addFriend()
        }
    }
    private fun getListOfFriends(userId:String) List<Friend>{
        // Use logged in user's id to get their list of friends.
    }
    private fun populateFriendsList(friendIds: Array<String>){

    }
    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
    private fun addFriend(userId:String){
        val friendIdEditText = findViewById(R.id.friendId) as EditText
        val friendId = friendIdEditText.text.toString()
        findPersonWithId(friendId)
    }
    private fun populateRequestList(friendRequestIds: Array<String>){

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
    private fun getIncomingFriendRequests(): Array<String> {
        val incomingRequestIds = arrayOf("hey","Here")
        return incomingRequestIds
    }
}