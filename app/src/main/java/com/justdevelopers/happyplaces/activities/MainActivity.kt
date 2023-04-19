package com.justdevelopers.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.justdevelopers.happyplaces.R
import com.justdevelopers.happyplaces.adapters.ItemAdapter
import com.justdevelopers.happyplaces.database.DatabaseHandler
import com.justdevelopers.happyplaces.databinding.ActivityMainBinding
import com.justdevelopers.happyplaces.models.HappyPlaceModel
import com.justdevelopers.happyplaces.utils.SwipeToDeleteCallBack
import com.justdevelopers.happyplaces.utils.SwipeToEditCallback
import com.karumi.dexter.PermissionToken

import com.karumi.dexter.listener.PermissionDeniedResponse

import com.karumi.dexter.listener.PermissionGrantedResponse

import com.karumi.dexter.listener.single.PermissionListener

import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest

class MainActivity : AppCompatActivity() {
    var binding:ActivityMainBinding? = null
    var mDeletedPlace:HappyPlaceModel ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)
        binding?.fabAddHappyPlace?.setOnClickListener {
            Log.i("dhiraj","dhiraj")
            startActivityForResult(Intent(this, AddHappyPlaceActivity::class.java),
                ADD_PLACE_ACTIVITY_REQUEST_CODE)
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
        setupHappyPlacesRecyclerView()

    }

     fun setupHappyPlacesRecyclerView() {
        val placesList = getHappyPlacesFromLocalDB()
        val adapter = ItemAdapter(this,placesList)
        binding?.rvHappyPlacesList?.adapter = adapter
        binding?.rvHappyPlacesList?.setHasFixedSize(true)
        binding?.rvHappyPlacesList?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.setOnClickListener(object :ItemAdapter.OnClickListener{
            override fun onClick(position: Int,model:HappyPlaceModel){
                val intent = Intent(this@MainActivity,HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })
        val rvHappyPlaceList = binding?.rvHappyPlacesList
        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // TODO (Step 5: Call the adapter function when it is swiped)
                // START
                val adapter = rvHappyPlaceList?.adapter as ItemAdapter
                adapter.notifyEditItem(
                    this@MainActivity,
                    viewHolder.adapterPosition,
                    ADD_PLACE_ACTIVITY_REQUEST_CODE
                )
                // END
            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rvHappyPlaceList)

        val deleteSwipeHandler=object:SwipeToDeleteCallBack(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvHappyPlaceList?.adapter as ItemAdapter

                adapter.notifyDeleteItem(
                    this@MainActivity,
                    viewHolder.adapterPosition)
                    setupHappyPlacesRecyclerView()
                Log.e("sizeof", placesList.size.toString())
                val contextView = binding?.rvHappyPlacesList as View
                Snackbar.make(contextView, "successfully deleted", Snackbar.LENGTH_LONG)
                    .setAction("undo") {
                        val db = DatabaseHandler(this@MainActivity)
                        db.addHappyPlace(mDeletedPlace!!)
                        db.close()
                        setupHappyPlacesRecyclerView()
                    }
                    .show()
            }
        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rvHappyPlaceList)
    }

    private fun getHappyPlacesFromLocalDB(): ArrayList<HappyPlaceModel> {
        val db=DatabaseHandler(this)

        val happyPlacesList = db.getHappyPlacesList()
        if(happyPlacesList.isNotEmpty()){
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
            binding?.rvHappyPlacesList?.visibility = View.VISIBLE
           return happyPlacesList
        }else{
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
            binding?.rvHappyPlacesList?.visibility = View.GONE
        }
        return ArrayList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                setupHappyPlacesRecyclerView()
            }else{
                Log.e("Activity","back pressed or cancelled")
            }
        }
    }

    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS="extra_place_details"
    }
}