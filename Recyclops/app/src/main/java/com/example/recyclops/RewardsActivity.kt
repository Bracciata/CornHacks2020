package com.example.recyclops

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.RelativeLayout
import com.google.gson.Gson


class RewardsActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRewards()
    }

    private fun setupRewards() {
        setContentView(R.layout.activity_rewards)

        val constraintLayout = findViewById(R.id.relativeLayoutRewards) as RelativeLayout
        val listView = ListView(this)
        val rewards = getRewards()
        val rewardStrings = mutableListOf<String>()
        for (reward in rewards) {
            rewardStrings.add("Buy ${reward.title} for ${reward.cost} on ${reward.timeAdded}")
        }

            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
                rewardStrings) as ListAdapter?

                    constraintLayout.addView(listView)

    }

    fun getRewards(): MutableList<Reward> {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val rewardsJson = sharedPreferences.getString("rewards_key", "{}")
        val rewardList: MutableList<Reward> = Gson().fromJson(rewardsJson, Array<Reward>::class.java).toMutableList()
        return rewardList
    }
}