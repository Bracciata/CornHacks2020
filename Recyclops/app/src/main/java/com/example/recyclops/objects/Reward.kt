package com.example.recyclops

import java.util.*

public class Reward(val costValue: Int, var saleCostValue: Int, val titleValue: String) {
    var cost: Int = costValue
    var saleCost: Int = saleCostValue
    var title: String = titleValue
    var timeAdded: Date = Date()
}
