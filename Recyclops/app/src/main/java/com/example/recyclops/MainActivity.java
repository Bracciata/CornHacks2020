package com.example.recyclops;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

class MainActivity : AppCompatActivity() {

private val REQUEST_CODE_PERMISSIONS = 101
private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA")

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
        textureView.post { startCamera() }
        textureView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        updateTransform()
        }
        } else {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        }

        override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
        ) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
        if (allPermissionsGranted()) {
        startCamera()
        } else {
        Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
        .show()
        finish()
        }
        }
        }

private fun allPermissionsGranted(): Boolean {

        for (permission in REQUIRED_PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(
        this,
        permission
        ) != PackageManager.PERMISSION_GRANTED
        ) {
        return false
        }
        }
        return true
        }
        }