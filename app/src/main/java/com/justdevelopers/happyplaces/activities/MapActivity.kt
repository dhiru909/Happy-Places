package com.justdevelopers.happyplaces.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.justdevelopers.happyplaces.R
import com.justdevelopers.happyplaces.databinding.ActivityMapBinding
import com.justdevelopers.happyplaces.models.HappyPlaceModel

class MapActivity : AppCompatActivity() ,OnMapReadyCallback{
    var binding: ActivityMapBinding? = null
    private var mHappyPlaceDetails:HappyPlaceModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mHappyPlaceDetails =  intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)

        }
        if(mHappyPlaceDetails!=null){
            setSupportActionBar(binding?.toolbarMap)
            if(supportActionBar!=null){
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)

            }
            binding?.toolbarMap?.setNavigationOnClickListener{
                onBackPressed()
            }
            supportActionBar?.title = mHappyPlaceDetails?.title
            val supportMapFragment = supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val position = LatLng(mHappyPlaceDetails!!.latitude,mHappyPlaceDetails!!.longitude)
        googleMap!!.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetails!!.location))
        val latLngZoom=CameraUpdateFactory.newLatLngZoom(position,10f)
        googleMap.animateCamera(latLngZoom)
    }
}