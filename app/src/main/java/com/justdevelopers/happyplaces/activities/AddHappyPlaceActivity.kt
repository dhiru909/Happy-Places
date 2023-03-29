package com.justdevelopers.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.justdevelopers.happyplaces.R
import com.justdevelopers.happyplaces.database.DatabaseHandler
import com.justdevelopers.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.justdevelopers.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var currentPhotoPath: String
    private var saveImageToInternalStorage:Uri? = null
    private var mLatitude:Double = 0.0
    private var mLongitude:Double = 0.0
    companion object{
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val IMAGE_DIRECTORY="HappyPlacesImages"

    }
    private val openGalleryLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result->
            if(result.resultCode == RESULT_OK && result.data!=null){
                val contentUri=result.data?.data
                val selectedImageBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,contentUri)
                lifecycleScope.launch {
                    saveImageToInternalStorage=saveImageToInternalStorage(selectedImageBitmap)
                    Log.e("saved image:","path:: $saveImageToInternalStorage")
                    runOnUiThread{
                        binding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)
                    }
                }
            }
        }
    private var binding:ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener:DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }
        binding?.etDate?.setOnClickListener(this)
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }
        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarAddPlace).setNavigationOnClickListener{
            onBackPressed()
        }
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)

    }

    override fun onBackPressed() {
        finish()

    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.et_date ->{
                DatePickerDialog(this@AddHappyPlaceActivity,
                    dateSetListener,cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tvAddImage ->{
                val pictureDialog = AlertDialog.Builder(this@AddHappyPlaceActivity)
//                pictureDialog.set(R.drawable.dialog_choose_background)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from gallery",
                "capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                        _, which->
                    run {
                        when (which) {
                            0 -> {
                                choosePhotoFromGallery()
                                if(isReadStorageAllowed()) {
                                    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                    openGalleryLauncher.launch(pickIntent)
                                }
                            }
                            1 -> {
                                Toast.makeText(
                                    this@AddHappyPlaceActivity,
                                    "camera opening",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if(isCameraAllowed()) {
                                    val pickIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    startActivityForResult(pickIntent, CAMERA_REQUEST_CODE)
                                }else{
                                    ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.CAMERA),
                                    CAMERA_PERMISSION_CODE
                                    )
                                }
                            }
                        }
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {

                when {
                    binding?.etTitle?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    binding?.etDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT)
                            .show()
                    }
                    binding?.etDate?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter date", Toast.LENGTH_SHORT)
                            .show()
                    }
                    binding?.etLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT)
                            .show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                        // Assigning all the values to data model class.
                        val happyPlaceModel = HappyPlaceModel(
                            0,
                            binding?.etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        // Here we initialize the database handler class.
                        val dbHandler = DatabaseHandler(this)

                        val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                        if (addHappyPlace > 0) {
                            Toast.makeText(
                                this,
                                "The happy place details are inserted successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish();//finishing activity
                        }
                    }
                }
            }
        }
    }

    private fun isCameraAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun isReadStorageAllowed():Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if(report.areAllPermissionsGranted()){
                        Toast.makeText(this@AddHappyPlaceActivity,"" +
                                "permission for storage granted",Toast.LENGTH_LONG).show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest> , token:PermissionToken ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("Permissions required, Enable them in setting")
            .setPositiveButton("go to settings"){
                _,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data=uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
            }
            }.setNegativeButton("cancel"){
                    dialog, _ ->
                try {
                    dialog.dismiss()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }.show()
    }

    private fun updateDateInView(){
        val myFormat="dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat,Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() &&grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                val pickIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(pickIntent, CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(this,
                "Please allow permission for camera in settings",
                Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_REQUEST_CODE){
                if(data != null)
                {
                    val image: Bitmap =data.extras!!.get("data") as Bitmap
                    lifecycleScope.launch {
                        saveImageToInternalStorage =saveImageToInternalStorage(image)
                        Log.e("saved image:","path:: $saveImageToInternalStorage")
                        runOnUiThread{
                            binding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)

                        }

                    }


                }
            }
        }
    }

    private suspend fun saveImageToInternalStorage(mBitmap: Bitmap):Uri{
        var wrapper =  ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        withContext(Dispatchers.IO){
            try {
                val stream: OutputStream = FileOutputStream(file)
                mBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                stream.flush()
                stream.close()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
        return Uri.parse(file.absolutePath)

    }


}