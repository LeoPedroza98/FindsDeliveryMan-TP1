package com.pedroza.finddeliveryman

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun GoRegistro(view: View) {
        val intent = Intent (this, ResultListActivity::class.java)
        startActivity(intent)
    }

    val REQUEST_PERMISSIONS_CODE = 1111

    private fun getCurrentCoordinates() {
        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val GPSOn = locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )

        if (!GPSOn) {
            Log.d("PermissionError", "Ative o GPS..")
        } else {
            if (GPSOn) {
                try {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10000L, 0f, locationListener
                    )
                } catch (ex: SecurityException) {
                    Log.d("PermissionError", "Erro de permissão")
                }
            }
        }
    }

    private val locationListener: LocationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val tempDate = Calendar.getInstance().time
                val formatData = SimpleDateFormat("HH_mm_ss-dd_MM_yyyy")
                val fileName = formatData.format(tempDate) + ".crd"
                var local = "${location.latitude} ${location.longitude}"

                try {
                    if (
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) &&
                            (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE
                            ))
                        ) {
                            callDialog(
                                "Conceda a permisão de escrever no sistema..",
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            )
                            callDialog(
                                "Conceda a permisão de ler no sistema..",
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                            )
                        } else {
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                REQUEST_PERMISSIONS_CODE
                            )
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_PERMISSIONS_CODE
                            )
                        }
                    } else {
                        if (isExternalStorageWritable() and isExternalStorageReadable()) {
                            val file = File(getExternalFilesDir(null), fileName)
                            BufferedWriter(FileWriter(file)).use {
                                it.write(local)

                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("File Error", "Não foi possível ler ou escrever o arquivo")
                }
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

    private fun callDialog(
        mensagem: String,
        permissions: Array<String>
    ) {
        var mDialog = AlertDialog.Builder(this)
            .setTitle("Permissão")
            .setMessage(mensagem)
            .setPositiveButton("Ok")
            { dialog, id ->
                ActivityCompat.requestPermissions(
                    this@MainActivity, permissions,
                    REQUEST_PERMISSIONS_CODE
                )
                dialog.dismiss()
            }
            .setNegativeButton("Cancel")
            { dialog, id ->
                dialog.dismiss()
            }
        mDialog.show()
    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    fun callAccessLocation(view: View?) {
        val permissionAFL = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionACL = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissionAFL != PackageManager.PERMISSION_GRANTED &&
                permissionACL != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                    )
            ) {
                callDialog(
                        "É preciso permitir acesso à localização!",
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_CODE
                )
            }
        } else {
            getCurrentCoordinates()
        }
    }
}