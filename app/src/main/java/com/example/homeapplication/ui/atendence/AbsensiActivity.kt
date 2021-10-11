package com.example.homeapplication.ui.atendence

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.homeapplication.MainActivity
import com.example.homeapplication.R
import com.example.homeapplication.data.model.AuthClockin
import com.example.homeapplication.data.model.AuthClockout
import com.example.homeapplication.ui.login.LoginActivity
import com.example.homeapplication.utils.ApiClientTest
import com.example.homeapplication.utils.ApiService
import com.example.homeapplication.utils.AppPreferences
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class AbsensiActivity : AppCompatActivity() {


    private val REQUEST_LOCATION = 1
    var latitude: String? = null
    var longitude: String? = null
    private var btnHadir: Button? = null
    private var btnKeluar: Button? = null
    private var llAbsenHadir: LinearLayout? = null
    private var llAbsenKeluar: LinearLayout? = null
    private var tvTanggal: TextView? = null
    private var tvAbsenMasuk: TextView? = null
    private var tvAbsenKeluar: TextView? = null
    var locationManager: LocationManager? = null

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi)
//        getLocation()
        getLastKnownLocation(this)
        AppPreferences.empId = "4"
        btnHadir = findViewById(R.id.btn_absen_hadir) as Button
        btnKeluar = findViewById(R.id.btn_absen_keluar) as Button
        llAbsenHadir = findViewById(R.id.ll_absen_hadir) as LinearLayout
        llAbsenKeluar = findViewById(R.id.ll_absen_keluar) as LinearLayout
        tvTanggal = findViewById(R.id.tv_tanggal_sekarang) as TextView
        tvAbsenMasuk = findViewById(R.id.tv_absen_masuk) as TextView
        tvAbsenKeluar = findViewById(R.id.tv_absen_keluar) as TextView
        var ivPhoto = findViewById(R.id.iv_photo) as ImageView

        val checkSelfPermission = ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            //Requests permissions to be granted to this application at runtime
            verifyStoragePermissions(this)
        }

        // absensi step
            if (AppPreferences.absen) {
                llAbsenHadir?.visibility = View.GONE
                llAbsenKeluar?.visibility = View.VISIBLE
            } else {
                llAbsenHadir?.visibility = View.VISIBLE
                llAbsenKeluar?.visibility = View.GONE
            }

        //date time
        val sdf = SimpleDateFormat("dd - M - yyyy ")
        val currentDate = sdf.format(Date())
        tvTanggal?.setText(currentDate)

        //photo
        if (AppPreferences.absenPhoto != ""){
            Picasso.get()
                .load(AppPreferences.absenPhoto)
                .into(ivPhoto)
//            Glide.with(this)
//                .load(AppPreferences.absenPhoto)
//                .into(ivPhoto)
        }else{
            ivPhoto.setImageResource(R.drawable.icon_profile_black)
        }



        btnHadir!!.setOnClickListener {
//           intentToAbsenLokasi()
            if (AppPreferences.absen) {

                showAlertDialogClockin("Anda Akan Clockout?")
            } else {
                showAlertDialogClockin("Anda Akan Clockin?")
            }
        }
        btnKeluar!!.setOnClickListener {
            if (AppPreferences.absen) {

                showAlertDialogClockin("Anda Akan Clockout?")
            } else {
                showAlertDialogClockin("Anda Akan Clockin?")
            }
        }

        tvAbsenMasuk?.setText(AppPreferences.absenMasuk)
        tvAbsenKeluar?.setText(AppPreferences.absenKeluar)
    }

    fun getLastKnownLocation(context: Context) {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        var location: Location? = null
        for (i in providers.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            location = locationManager.getLastKnownLocation(providers[i])
            if (location != null)
                break
        }
        val gps = DoubleArray(2)
        if (location != null) {
            gps[0] = location.getLatitude()
            gps[1] = location.getLongitude()
            latitude = gps[0].toString()
            longitude = gps[1].toString()
            Log.e("gpsLat", gps[0].toString())
            Log.e("gpsLong", gps[1].toString())

        }

    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {
            val locationGPS: Location =
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
            val lat = locationGPS.latitude
            val longi = locationGPS.longitude
            latitude = lat.toString()
            longitude = longi.toString()
            Log.e("latitude", latitude.toString())
            Log.e("longitude", longitude.toString())
        }
    }

    fun Clockin() {
        var apiService1: ApiService
//        apiService1 = ApiClientSimple().services
        apiService1 = ApiClientTest.ServiceBuilder.buildService(ApiService::class.java)
        apiService1.Clockin(
            AppPreferences.empId.toString(),
            latitude.toString(),
            longitude.toString()
        )
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { users ->
                    getData(users)

                }, { error ->
                    Log.e("error", error.localizedMessage)
                    showAlertDialog("Terjadi Kesalah, Coba Lagi")
                }
            )
    }

    fun getData(users: AuthClockin?) {
        Log.e("AuthClockin", users.toString())
//        showAlertDialog(users?.meta?.message.toString())
//        if (users?.meta?.message.equals("absent exist")){
//            showAlertDialog(users?.meta?.message.toString())
//        }else{
            llAbsenHadir?.visibility = View.GONE
            llAbsenKeluar?.visibility = View.VISIBLE
            AppPreferences.absen = true
            AppPreferences.absenMasuk = users?.result?.attedance?.clockin
            var tmpLocation =
                users?.result?.location?.desa + "," + users?.result?.location?.kabupaten + "," + users?.result?.location?.kecamatan + "," + users?.result?.location?.provinsi
            AppPreferences.absenLocation = tmpLocation
            intentToAbsenLokasi(users?.meta?.message.toString())
//        }

//        Log.e("AuthClockin", users?.result?.get(0)?.location?.get(0)?.desa.toString())
    }

    fun intentToAbsenLokasi(message: String) {
        val intent = Intent(this, AbsenLokasiActivity::class.java)
        intent.putExtra("clockinmessage", message)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        finish()
        startActivity(intent)
    }

    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this,
                        "Cannot write images to external storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun Clockout() {
        var apiService1: ApiService
//        apiService1 = ApiClientSimple().services
        apiService1 = ApiClientTest.ServiceBuilder.buildService(ApiService::class.java)
        apiService1.ClockOut(AppPreferences.empId.toString())
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { users ->
                    getDataClockout(users)

                }, { error ->
                    Log.e("error", error.localizedMessage)
                    showAlertDialog("Terjadi Kesalah, Coba Lagi")
                }
            )
    }

    fun getDataClockout(users: AuthClockout?) {
        if (users?.meta?.status.equals("success")){

            Log.e("AuthClocout", users.toString())
            showAlertDialog(users?.meta?.message.toString())
            val sdf = SimpleDateFormat("dd - M - yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            AppPreferences.absen = false
            AppPreferences.absenKeluar = currentDate
            llAbsenHadir?.visibility = View.VISIBLE
            llAbsenKeluar?.visibility = View.GONE
            btnHadir?.setText("Absen Masuk")
            tvAbsenKeluar?.setText(AppPreferences.absenKeluar)

        }
        else{
            showAlertDialog(users?.meta?.message.toString())
        }

    }

    fun showAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, which ->
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showAlertDialogClockin(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage(message)
        builder.setPositiveButton("Ok") { dialog, which ->
            if (AppPreferences.absen) {
                Clockout()
            } else {
                Clockin()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}