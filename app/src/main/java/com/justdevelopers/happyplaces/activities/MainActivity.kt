package com.justdevelopers.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.justdevelopers.happyplaces.adapters.ItemAdapter
import com.justdevelopers.happyplaces.database.DatabaseHandler
import com.justdevelopers.happyplaces.databinding.ActivityMainBinding
import com.justdevelopers.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest

class MainActivity : AppCompatActivity() {
    var binding:ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)
        binding?.fabAddHappyPlace?.setOnClickListener {
            Log.i("dhiraj","dhiraj")
            startActivity(Intent(this, AddHappyPlaceActivity::class.java))
        }
//        Dexter.withContext(this)
//            .withPermission(android.Manifest.permission.CAMERA)
//            .withListener(object : PermissionListener {
//                override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
//                    Toast.makeText(this@MainActivity,"Dhiraj",Toast.LENGTH_LONG).show()
//                }
//
//                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
//                    Toast.makeText(this@MainActivity,"Khali",Toast.LENGTH_LONG).show()
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permission: PermissionRequest?,
//                    token: PermissionToken?
//                ) { /* ... */
//                }
//            }).check()
       val placesList=getHappyPlacesFromLocalDB()
        val adapter = ItemAdapter(placesList)
        binding?.rvHappyPlacesList?.adapter = adapter
        binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        if(placesList.isNotEmpty()) {
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
            binding?.rvHappyPlacesList?.visibility =View.VISIBLE
        }

    }

    private fun getHappyPlacesFromLocalDB(): ArrayList<HappyPlaceModel> {
        val db=DatabaseHandler(this)

        val happyPlacesList = db.getHappyPlacesList()
        if(happyPlacesList.isNotEmpty()){
           return happyPlacesList
        }
        return ArrayList()
    }
}