package com.example.recyclops;

import java.lang.reflect.Method;
import java.util.Date;

public class User {
    var friends : List<User> = listOf<User>()
    var name: String = ""
    var email:String=""
    var password:String=""
    var points: Int = 0
    var userCreationTime:Date=  Date()
    var  redemptionHistory: List<Redemption> = listOf<Redemption>()
    var userId: String = ""
    var friendRequestsIncomingUserIds: List<String> = listOf<String>()


    public fun getId():String{
        return userId;
    }
}
