package com.justdevelopers.happyplaces.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.justdevelopers.happyplaces.R
import com.justdevelopers.happyplaces.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    var binding: ActivityMapBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarMap)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }
        binding?.toolbarMap?.setNavigationOnClickListener{
            onBackPressed()
        }
    }
}