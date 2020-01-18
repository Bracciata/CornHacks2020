package com.example.recyclops;

import java.util.Date;

public class Redemption (reward:Reward){
    var rewardRedeemed:Reward = reward
    // Save cost separately because the cost may change in the future
    var purchaseCost:Int=reward.cost
    var redemptionTime:Date=Date()
}
