package com.example.recyclops;

import java.lang.reflect.Method;
import java.util.Date;

class User(val fName:String, var lName:String,val emailAdd:String,var passwordHash:String, val userIdentification:String) {
    var friends : MutableList<User> = mutableListOf<User>()
    var firstName: String = fName
    var lastName: String = lName
    var email:String=emailAdd
    private var password:String=passwordHash // In the future this will be a hash based value.
    var points: Int = 0
    var userCreationTime:Date=  Date()
    var  redemptionHistory: MutableList<Redemption> = mutableListOf<Redemption>()
    private var userId: String = userIdentification
    var friendRequestsIncomingUserIds: MutableList<String> = mutableListOf<String>()
    var totalPoints: Int =0


    fun getId():String{
        return userId;
    }
    fun checkPassword(passwordAttempt:String):Boolean{
        return passwordAttempt == password
    }
    fun addFriend(friend:User){
        friends.add(friend)
    }
    fun redeemPrize(prize:Reward){
        redemptionHistory.add(Redemption(prize))
        changePoints(-prize.saleCost)
    }
    fun changePoints(numberOfPoints:Int){
        points +=numberOfPoints
        if(numberOfPoints>0){
            totalPoints += numberOfPoints
        }
    }
    fun addRequest(id:String){
        friendRequestsIncomingUserIds.add(id)
    }
}
