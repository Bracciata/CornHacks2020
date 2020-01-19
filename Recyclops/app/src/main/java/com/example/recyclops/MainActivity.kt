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
import android.content.SharedPreferences
import com.google.gson.Gson
import android.content.Context
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var lensFacing = CameraX.LensFacing.BACK
    private val TAG = "MainActivity"

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA")

    private var tfLiteClassifier: TFLiteClassifier = TFLiteClassifier(this@MainActivity)
    private val sharedPrefFile = "kotlinsharedpreference"

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
        rewards.add(Reward(1221,234, "Amazon 1000 Dollar Gift Card"))
        rewards.add(Reward(40,10, "Reusable Water Bottle"))
        rewards.add(Reward(8,8, "Metal Straw"))
        rewards.add(Reward(10,1, "Donate A Tree"))
        updateRewards(rewards)
        return rewards
    }
    private fun createUsers(rewards: List<Reward>):List<User>{
        var users =  mutableListOf<User>()
        users.add(User("Johnny","Carson","JohnnyC@unl.edu","Acting2019","1"))
        users.add(User("Dick","Carson","DickC@unl.edu","Acting2019","2"))
        users[0].addFriend(users[1])
        users[0].redeemPrize(rewards[0])
        users[0].redeemPrize(rewards[1])
        users[0].redeemPrize(rewards[3])
        users.add(User("Alexis","Maas","MaasA@unl.edu","Acting2019","3"))
        users[0].addRequest(users[2].getId())
        // This user wil be added as a friend when demoing
        users.add(User("FriendFirstNameToAdd","MEEEEE","Faker@unl.edu","Acting2019","4"))

        updateUsers(users)
        return users
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


        var toolbar :Toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        var drawerLayout:DrawerLayout = findViewById(R.id.drawer_layout)
        var navView : NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.app_name, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        invalidateOptionsMenu()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // TODO: Update this
        var drawerLayout:DrawerLayout = findViewById(R.id.drawer_layout)

        when (item.itemId) {
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_rewards -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_leaderboards -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_view -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_map -> {
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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
    fun updateRewards(rewards:List<Reward>){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val rewardsJson = Gson().toJson(rewards)
        editor.putString("rewards_key",rewardsJson)
    }
    fun getRewards():List<Reward>{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val rewardsJson = sharedPreferences.getString("rewards_key","{}")
        val rewardList:  MutableList<Reward> = Gson().fromJson(rewardsJson, Array<Reward>::class.java).toMutableList()
        return rewardList

    }
    fun updateUsers(users:List<User>){
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        val usersJson = Gson().toJson(users)
        editor.putString("users_key",usersJson)
    }
    fun getUsers():List<User>{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("users_key","{}")
        val userList:  MutableList<User> = Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
        return userList
    }
    fun getSignedInUser(): User{
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString("active_user_key","{}")
        val user :  User = Gson().fromJson(userJson, User::class.java)
        return user
    }



    override fun onDestroy() {
        tfLiteClassifier.close()
        super.onDestroy()
    }
}