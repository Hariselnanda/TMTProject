package com.example.homeapplication.ui.atendence

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.homeapplication.R
import com.example.homeapplication.data.model.AuthClockin
import com.example.homeapplication.data.model.AuthPhotoClockin
import com.example.homeapplication.helper.LoadingBar
import com.example.homeapplication.utils.ApiClientSimple
import com.example.homeapplication.utils.ApiClientTest
import com.example.homeapplication.utils.ApiService
import com.example.homeapplication.utils.AppPreferences
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.*
import java.util.*

class AbsenLokasiActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    private var mUri: Uri? = null
    var pleaseWait: ProgressDialog? = null
    private val OPERATION_CAPTURE_PHOTO_RUMAH = 1
    private val REQUEST_CAMERA = 2
    private val PERMISSIONS_CAMERA =
        arrayOf(Manifest.permission.CAMERA)
    var tmpBase64Rumah: String = ""
    private var btnLanjutAbsensi: Button? = null
    private var tvLokasi: TextView? = null
    private var tvAbsenMasuk: TextView? = null
    private var tvAbsenLokasi: TextView? = null

    var pLatitude: String? = null
    var pLongitude: String? = null
    //maps

    private lateinit var mMap: GoogleMap
    var MY_PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var mGoogleApiClient: GoogleApiClient
    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    var mLocationRequest: LocationRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absen_lokasi)
        verifyCameraPermissions(this)
        pleaseWait = LoadingBar.show(this)
        AppPreferences.absenStepTwo = true
        val ss: String = intent.getStringExtra("clockinmessage").toString()
        btnLanjutAbsensi = findViewById(R.id.btn_lanjut_absen_lokasi) as Button
        tvLokasi = findViewById(R.id.tv_lokasi_anda) as TextView
        tvAbsenMasuk = findViewById(R.id.tv_waktu_absen) as TextView
        tvAbsenLokasi = findViewById(R.id.tv_waktu_lokasi) as TextView
        btnLanjutAbsensi!!.setOnClickListener {
            val checkSelfPermission = ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            )
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                //Requests permissions to be granted to this application at runtime

                verifyCameraPermissions(this)
            } else {
                capturePhotoRumah()
            }
        }
//        tvLokasi!!.setText(ss)
        tvAbsenMasuk?.setText(AppPreferences.absenMasuk)
        tvAbsenLokasi?.setText(AppPreferences.absenLocation)

        //maps
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
//        Setup()
    }

    fun Clockin() {
        pleaseWait?.show()
        var apiService1: ApiService
        apiService1 = ApiClientTest.ServiceBuilder.buildService(ApiService::class.java)
        apiService1.Clockin(
            AppPreferences.empId.toString(),
            pLatitude.toString(),
            pLongitude.toString()
        )
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { users ->
                    getData(users)

                }, { error ->
                    pleaseWait?.dismiss()
                    Log.e("error", error.localizedMessage)
                    showAlertDialog("Terjadi Kesalah, Coba Lagi")
                }
            )
    }


    fun getData(users: AuthClockin?) {
        pleaseWait?.dismiss()
        Log.e("AuthClockin", users.toString())
        showAlertDialog(users?.meta?.message.toString())
        tvLokasi?.text = users?.meta?.message.toString()
        AppPreferences.absen = true
        Log.e("AuthClockin", users?.meta?.message.toString())
    }

    fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, which ->
        }
//        builder.setNegativeButton("Cancel") { dialog, which ->
//        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun capturePhotoRumah() {
        val capturedImage = File(externalCacheDir, "Photo_rumah.jpg")
        if (capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()
        mUri = if (Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(
                this, "com.example.homeapplication.fileprovider",
                capturedImage
            )
        } else {
            Uri.fromFile(capturedImage)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO_RUMAH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OPERATION_CAPTURE_PHOTO_RUMAH ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(mUri!!)
                    )
//                    saveImageToExternalStorage(bitmap, "RUMAH")
//                    iv_foto_rumah!!.setImageBitmap(bitmap)
                    tmpBase64Rumah = encodeImage(bitmap).toString()
                    ClockinPhoto(AppPreferences.empId.toString(), tmpBase64Rumah)
//                    Log.e("tmpbase64rumah", tmpBase64Rumah)
//                    AppPreferences.absenStepTwo = false
//                    finish()
                }
        }
    }

    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun verifyCameraPermissions(activity: Activity?) {
        // Check if we have write permission
        val permissionCamera = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.CAMERA
        )
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_CAMERA,
                REQUEST_CAMERA
            )
        }
    }


    fun Setup() {
        Clockin()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                === PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mMap.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap.isMyLocationEnabled = true
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            === PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest, this
            )
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            === PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest, this
            )
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location?) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        val latLng = LatLng(location!!.latitude, location!!.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(), true)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            !== PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locations = provider?.let { locationManager.getLastKnownLocation(it) }
        val providerList =
            locationManager.allProviders
        if (null != locations && null != providerList && providerList.size > 0) {
            val longitude = locations.longitude
            val latitude = locations.latitude
            pLatitude = latitude.toString()
            pLongitude = longitude.toString()
            val geocoder = Geocoder(
                applicationContext,
                Locale.getDefault()
            )
            try {
                val listAddresses =
                    geocoder.getFromLocation(
                        latitude,
                        longitude, 1
                    )
                if (null != listAddresses && listAddresses.size > 0) {
                    val state = listAddresses[0].adminArea
                    val country = listAddresses[0].countryName
                    val subLocality = listAddresses[0].subLocality
                    markerOptions.title(
                        "" + latLng + "," + subLocality + "," + state
                                + "," + country
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        mCurrLocationMarker = mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
            )
        }
    }

    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.size <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this,
                        "Cannot write camera",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        === PackageManager.PERMISSION_GRANTED
                    ) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(
                        this, "permission denied",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun addJpgToGallery(imageFile: String): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("Attendence"),
                String.format("Attendence_%d.jpg", System.currentTimeMillis())
            )
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), photo)

            var imagename =
                MultipartBody.Part.createFormData("Attendence_image", photo.getName(), requestFile)


//            saveBitmapToJPG(signature, photo)

//            uploadSignature(AppPreferences.token, tmpID, et_notes.text.toString(), imagename)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    fun getAlbumStorageDir(albumName: String?): File? {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("Attendence", "Directory not created")
        }
        return file
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    //    private fun uploadSignature(id : String,imageFile :MultipartBody.Part){
//        pleaseWait?.show()
//        var mId =   RequestBody.create(MediaType.parse("text/plain"), id)
////        var mNotes =   RequestBody.create(MediaType.parse("text/plain"), notes)
//        // init retrofit
//
////        var apiService1: ApiService
////        apiService1 = ApiClientTest.ServiceBuilder.buildService(ApiService::class.java)
////        val call = apiService1.PhotoClockin(id, imageFile)
//
//        var apiService1: ApiService
//        apiService1 = ApiClientSimple().services
//        val call = apiService1.PhotoClockin(id, imageFile)
//        // membaut request ke api
//        call.enqueue(object : Callback<AuthPhotoClockin>{
//
//            // handling request saat fail
//            override fun onFailure(call: Call<AuthPhotoClockin>?, t: Throwable?) {
//                pleaseWait?.dismiss()
//                showAlertDialog("Terjadi Kesalahan, Coba lagi")
//                Log.d("ONFAILURE",t.toString())
//            }
//
//            // handling request saat response.
//            override fun onResponse(call: Call<AuthPhotoClockin>?, response: Response<AuthPhotoClockin>?) {
//
//                // dismiss progress dialog
//                pleaseWait?.dismiss()
////                Log.d("message", response?.body()?.message)
//                Log.d("body", response?.body().toString())
//                Log.d("code", response?.code().toString())
//
//
//                // saat pesan mempunyai kata 'success' maka tutup/akhiri activity ini.
////                if(response?.body()?.message?.contains("Success",true)!!){
////                }
//
//            }
//
//
//        })
//
//    }
    fun ClockinPhoto(id: String, imageFile: String) {
        pleaseWait?.show()

//        var mId = RequestBody.create(MediaType.parse("text/plain"), id)

        var apiService1: ApiService
        apiService1 = ApiClientTest.ServiceBuilder.buildService(ApiService::class.java)
        apiService1.PhotoClockin(id, imageFile)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { users ->
                    getDataPhoto(users)

                }, { error ->
                    pleaseWait?.dismiss()
                    Log.e("error", error.localizedMessage)
                    showAlertDialog("Terjadi Kesalah, Coba Lagi")
                }
            )
    }


    fun getDataPhoto(users: AuthPhotoClockin?) {
        pleaseWait?.dismiss()
        if (users?.meta?.status.equals("success")){
            Toast.makeText(this,users?.meta?.message,Toast.LENGTH_SHORT).show()
            AppPreferences.absenStepTwo = false
            AppPreferences.absenPhoto = users?.result?.image
            val intent = Intent(this, AbsensiActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            startActivity(intent)
        }else{
            Toast.makeText(this,users?.meta?.message,Toast.LENGTH_SHORT).show()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}