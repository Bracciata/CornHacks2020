package com.example.recyclops

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.util.Size
import android.view.Surface
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private var lensFacing = CameraX.LensFacing.BACK
    private val TAG = "MainActivity"

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA")

    private var tfLiteClassifier: TFLiteClassifier = TFLiteClassifier(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openMain()
        val rewards = createRewards()
        val users = createUsers(rewards)
    }
    private fun createRewards():List<Reward>{
        var rewards =  mutableListOf<Reward>()
        // If sale price is equal to price or greater than it is considered not on sale.
        rewards.add(Reward(7,4, "Amazon 5 Dollar Gift Card"))
        rewards.add(Reward(7,4, "Amazon 5 Dollar Gift Card"))
        rewards.add(Reward(7,4, "Amazon 5 Dollar Gift Card"))
        rewards.add(Reward(7,4, "Amazon 5 Dollar Gift Card"))
        rewards.add(Reward(7,4, "Amazon 5 Dollar Gift Card"))
        rewards.add(Reward(7,4, "Amazon 5 Dollar Gift Card"))
        rewards.add(Reward(7,4, "Amazon 5 Dollar Gift Card"))
    }
    private fun createUsers(rewards: List<Reward>):List<User>{
        var users =  mutableListOf<User>()
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
    }

    private fun openMain() {
        // Open camera screen
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            textureView.post { startCamera() }
            textureView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateTransform()
            }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        tfLiteClassifier
            .initialize()
            .addOnSuccessListener { }
            .addOnFailureListener { e -> Log.e(TAG, "Error in setting up the classifier.", e) }
    }


    private fun startCamera() {
        val metrics = DisplayMetrics().also { textureView.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = Rational(1, 1)

        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetResolution(screenSize)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(windowManager.defaultDisplay.rotation)
            setTargetRotation(textureView.display.rotation)
        }.build()

        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            textureView.surfaceTexture = it.surfaceTexture
            updateTransform()
        }


        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // Use a worker thread for image analysis to prevent glitches
            val analyzerThread = HandlerThread("AnalysisThread").apply {
                start()
            }
            setCallbackHandler(Handler(analyzerThread.looper))
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()


        val analyzerUseCase = ImageAnalysis(analyzerConfig)
        analyzerUseCase.analyzer =
            ImageAnalysis.Analyzer { image: ImageProxy, rotationDegrees: Int ->

                val bitmap = image.toBitmap()

                tfLiteClassifier
                    .classifyAsync(bitmap)
                    .addOnSuccessListener { resultText -> predictedTextView?.text = resultText }
                    .addOnFailureListener { error -> }

            }
        CameraX.bindToLifecycle(this, preview, analyzerUseCase)
    }

    fun ImageProxy.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun updateTransform() {
        val matrix = Matrix()
        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        val rotationDegrees = when (textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        textureView.setTransform(matrix)
    }
    private fun openProfile(){
        // Check if logged in
        val loggedIn = false; // TODO: Implement this
        if(loggedIn){
            openProfileConfirmed()
        }else{
            openLogIn()
        }
    }
    private fun openProfileConfirmed(){
        // val intent = Intent(this, Profile::class.java)
        // start your next activity
        startActivity(intent)
    }
    private fun openLogIn(){
        val intent = Intent(this, LogInActivity::class.java)
        // start your next activity
        startActivity(intent)

    }
    private fun openGuide(){
        val intent = Intent(this, GuideActivity::class.java)
        // start your next activity
        startActivity(intent)
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


    override fun onDestroy() {
        tfLiteClassifier.close()
        super.onDestroy()
    }
}