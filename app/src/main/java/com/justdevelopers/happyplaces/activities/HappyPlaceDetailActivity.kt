package com.justdevelopers.happyplaces.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.justdevelopers.happyplaces.R
import com.justdevelopers.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.justdevelopers.happyplaces.databinding.ActivityHappyPlaceDetailBinding
import com.justdevelopers.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.Dispatchers.Main

class HappyPlaceDetailActivity : AppCompatActivity() {

//    val scaletype=ImageView.ScaleType!("centerCrop")
    var happyPlaceDetailModel:HappyPlaceModel? = null
    private var binding:ActivityHappyPlaceDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }
        binding?.toolbarAddPlace?.setNavigationOnClickListener{
            onBackPressed()
        }
        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceDetailModel= intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)!!

        }
        if( happyPlaceDetailModel!=null ){
            setSupportActionBar(binding?.toolbarAddPlace)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailModel!!.title
            binding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetailModel!!.image))
            binding?.tvDescription?.text=happyPlaceDetailModel!!.description
            binding?.tvLocation?.text=happyPlaceDetailModel!!.location
            binding?.toolbarAddPlace!!.setNavigationOnClickListener{
                onBackPressed()
            }
            binding?.btnViewOnMap?.setOnClickListener {
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlaceDetailModel)
                startActivity(intent)
            }
        }

    }

    override fun onBackPressed() {
        finish()
    }
}

