package com.example.facedecorater

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.facedecorater.ar.ArTest
import com.google.ar.core.ArCoreApk

class MainActivity : AppCompatActivity() {

    companion object {
        private val PERMISSIONS = arrayOf(
            android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.CAMERA
        )
        private const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermissionIsGranted()) {
            val intent = Intent(this, ArTest::class.java)
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE)
        }
    }

    private fun checkPermissionIsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (!checkPermissionIsGranted()) {
                val alertBuilder = AlertDialog.Builder(this)
                alertBuilder.apply {
                    setTitle("Permissions are rejected")
                    setMessage("Please Allow Permissions")
                    setPositiveButton("Yes") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            PERMISSIONS,
                            REQUEST_CODE
                        )
                    }
                }.create().apply {
                    setCanceledOnTouchOutside(false)
                }.run { show() }
            }
        }
    }

}