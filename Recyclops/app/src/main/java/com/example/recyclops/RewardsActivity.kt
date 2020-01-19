package com.example.recyclops

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson


class RewardsActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRewards()
    }

    private fun setupRewards() {
        setContentView(R.layout.activity_rewards)
        var toolbar : Toolbar = findViewById(R.id.toolbarRewards)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
    private fun returnToMain(){
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
    fun getRewards(): MutableList<Reward> {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val rewardsJson = sharedPreferences.getString("rewards_key", "{}")
        val rewardList: MutableList<Reward> = Gson().fromJson(rewardsJson, Array<Reward>::class.java).toMutableList()
        return rewardList
    }
}