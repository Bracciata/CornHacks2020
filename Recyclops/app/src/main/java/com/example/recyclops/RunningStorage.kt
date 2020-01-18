package com.example.recyclops;

class RunningStorage {
    //This will be a running storage for while the app is running
    // This is only temporary and because of the time constraints of a hackathon
    // In the future we will be using a database that does not contain plain text passwords.
    var users: List<User> = listOf<User>()
    var rewards: List<Reward> = listOf<Reward>()

    fun getUsers(): MutableList<User>(){
        return users
    }
    fun setUsers(userList:MutableList<User>){
        users = userList
    }
    fun getRewards(): mutableListOf<Reward>(){
        return rewards
    }
    fun setRewards(rewardsList:MutableList<Reward>){
        rewards = rewardsList
    }
}
