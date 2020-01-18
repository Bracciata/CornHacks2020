package com.example.recyclops;

import java.lang.reflect.Method;
import java.util.Date;

class User(val fName:String, var lName:String,val emailAdd:String,var passwordHash:String, val userIdentification:String) {
    var friends : List<User> = listOf<User>()
    var firstName: String = fName
    var lastName: String = lName
    var email:String=emailAdd
    private var password:String=passwordHash // In the future this will be a hash based value.
    var points: Int = 0
    var userCreationTime:Date=  Date()
    var  redemptionHistory: List<Redemption> = listOf<Redemption>()
    private var userId: String = userIdentification
    var friendRequestsIncomingUserIds: MutableList<String> = mutableListOf<String>()


    fun getId():String{
        return userId;
    }
    fun checkPassword(passwordAttempt:String):Boolean{
        return passwordAttempt==password
    }
    fun addFriend(friend:User){

    }
    fun redeemPrize(prize:Reward){

    }
    fun changePoints(numberOfPoints:Int){
        points +=numberOfPoints
    }
    fun addRequest(id:String){
        friendRequestsIncomingUserIds.add(id)
    }
}
