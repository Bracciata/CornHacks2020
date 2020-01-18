package com.example.recyclops

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.ArrayAdapter
import android.widget.ListView


class RewardsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRewards()
    }

    private fun setupRewards() {
        setContentView(R.layout.activity_rewards)

        val constraintLayout = findViewById(R.id.relativeLayout) as RelativeLayout

        val listView = ListView(this)

        val values = arrayOf(
            "Rick and Morty",
            "Gaeme of Thrones",
            "Silicon Valley",
            "IT Crowd",
            "Person of Interest")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values)

        listView.adapter = adapter

        constraintLayout.addView(listView)

    }
}