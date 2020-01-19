package com.example.recyclops

import android.annotation.SuppressLint
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
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.lang.Exception

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val recyclableItems = arrayOf(
        "paper", "newspaper", "cardboard", "plastic", "phonebooks",
        "magazines", "mail", "tin", "aluminum", "steel", "glass", "soft drink", "beer bottles",
        "wine", "liquor", "beer bottle", "beer glass", "plastic bag", "pop bottle", "water bottle"
    )
    private var lensFacing = CameraX.LensFacing.BACK
    private val tag = "MainActivity"
    private val requestPermission = 101
    private val requiredPermission = arrayOf("android.permission.CAMERA")
    private var tfLiteClassifier: TFLiteClassifier = TFLiteClassifier(this@MainActivity)
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Opens the main activity and prepares the image recognition.
        openMain()
        // Populates the application with template content
        val rewards = createRewards()
        createUsers(rewards)
    }

    private fun createRewards(): List<Reward> {
        // Creates template reward items.
        val rewards = mutableListOf<Reward>()
        // If sale price is equal to price or greater than it is considered not on sale.
        rewards.add(Reward(500, 500, "Amazon 5 Dollar Gift Card"))
        rewards.add(Reward(90000, 90000, "Amazon 1000 Dollar Gift Card"))
        rewards.add(Reward(1500, 1500, "Reusable Water Bottle"))
        rewards.add(Reward(300, 300, "Metal Straw"))
        rewards.add(Reward(250, 50, "Donate A Tree"))
        updateRewards(rewards)
        return rewards
    }

    private fun createUsers(rewards: List<Reward>): List<User> {
        // Creates template users for leaderboards.
        val users = mutableListOf<User>()
        users.add(User("Johnny", "Carson", "JohnnyC@unl.edu", "Acting2019", "1"))
        users.add(User("Dick", "Carson", "DickC@unl.edu", "Acting2019", "2"))
        users[0].addFriend(users[1])
        users[0].redeemPrize(rewards[0])
        users[0].redeemPrize(rewards[1])
        users[0].redeemPrize(rewards[3])
        users.add(User("Alexis", "Maas", "MaasA@unl.edu", "Acting2019", "3"))
        users[0].addRequest(users[2].getId())
        // This user wil be added as a friend when demoing
        users.add(User("FriendFirstNameToAdd", "MEEEEE", "Faker@unl.edu", "Acting2019", "4"))
        users.add(User("Johnny", "Bikedaughter", "JBikedaugher@unl.edu", "Acting2019", "5"))
        users[0].addFriend(users[4])
        users[4].changePoints(14)
        users[1].changePoints(7)
        users[2].changePoints(11)
        users[0].changePoints(90900)

        updateUsers(users)
        return users
    }

    private fun openMain() {
        // Open camera screen
        setContentView(R.layout.activity_main)
        // Ask for permissions to use the camera
        if (allPermissionsGranted()) {
            textureView.post { startCamera() }
            textureView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateTransform()
            }
        } else {
            ActivityCompat.requestPermissions(this, requiredPermission, requestPermission)
        }
        // Set up the tensorflow model.
        tfLiteClassifier
            .initialize()
            .addOnSuccessListener { }
            .addOnFailureListener { e -> Log.e(tag, "Error in setting up the classifier.", e) }

        // Populates the side navigation and header.
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.app_name, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        invalidateOptionsMenu()
    }

    @SuppressLint("SetTextI18n")
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        try {
            val item: MenuItem = menu.findItem(R.id.nav_profile)
            // Check if user is signed in and if so add their name to the text.
            // If not then state not logged in.
            val nameNavText: TextView = findViewById(R.id.nameNav)
            val pointsNavText: TextView = findViewById(R.id.pointsNav)
            val user = getSignedInUser()
            if (user.userIdentification !== "-1") {
                // Add the user information near the nav drawer
                nameNavText.text = "${user.firstName} ${user.lastName}"
                pointsNavText.text = "Points: ${user.points}"
                // Change the text of the profile log in item based on whether or not signed in
                item.title = "Profile"
            } else {
                // State not logged in
                nameNavText.text = "Not signed in"
                pointsNavText.text = "Sign in to save your storage"
                // Change the text of the profile log in item based on whether or not signed in
                item.title = "Log In or Register"
            }
        } catch (e: Exception) {
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Add selections for on-click events from the drop-down menu.
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        // Allow the items in side nav to link to their respective locations.
        when (item.itemId) {
            R.id.nav_profile -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                openProfile()
            }
            R.id.nav_map -> {
                Toast.makeText(this, "Map clicked", Toast.LENGTH_SHORT).show()
                openMap()
            }
            R.id.nav_leaderboards -> {
                Toast.makeText(this, "Leaderboards clicked", Toast.LENGTH_SHORT).show()
                openLeaderboard()
            }
            R.id.nav_rewards -> {
                Toast.makeText(this, "Rewards clicked", Toast.LENGTH_SHORT).show()
                openRewards()
            }
            R.id.nav_guide -> {
                Toast.makeText(this, "Guide clicked", Toast.LENGTH_SHORT).show()
                openGuide()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun startCamera() {
        // Basic camera screen configuration.
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
            ImageAnalysis.Analyzer { image: ImageProxy, _: Int ->

                val bitmap = image.toBitmap()

                tfLiteClassifier
                    .classifyAsync(bitmap)
                    .addOnSuccessListener { resultText -> checkResult(resultText.toString()) }
                    .addOnFailureListener { error -> Log.e("IMAGE ERROR", error.toString()) }

            }
        // Starts the camera
        CameraX.bindToLifecycle(this, preview, analyzerUseCase)
    }

    private fun checkResult(resultText: String) {
        // Check to see if the object captured by the camera is recyclable.
        val predictedTextView = findViewById<TextView>(R.id.predictedTextView)
        predictedTextView.text = resultText
        var resultNumber = resultText.split(".")[1]
        resultNumber = ".$resultNumber"
        val floatResults = resultNumber.toFloat()
        // Note .8 is a magic number representing the odds of it being correct are over 80%
        if (floatResults >= .8) {
            // Check if on recyclable list
            val remainder = resultText.split("\n")[0].substring(14)
            if (isTermOnRecycleList(remainder)) {
                // Pause camera and create builder dialog.
                CameraX.unbindAll()
                val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
                builder.setTitle("Recycle?")
                builder.setMessage("Would you like to recycle the $remainder for one point?")
                // The user decides to recycle it
                builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
                    // Check if the user is signed in
                    val user = getSignedInUser()
                    if (user.userIdentification !== "-1") {
                        // The user is signed in so recycle the item(add a point)
                        Toast.makeText(
                            applicationContext,
                            "Recycled. You got one point!", Toast.LENGTH_SHORT
                        ).show()
                        recycleItem(user)
                        // Restart the camera
                        startCamera()
                    } else {
                        // Because the user is not signed in we send them to the log in page.
                        Toast.makeText(
                            applicationContext,
                            "You need to log in first!", Toast.LENGTH_SHORT
                        ).show()
                        openLogIn()
                    }
                    dialog.dismiss()
                }
                // They do not want to recycle it so restart the camera and proceed scanning
                builder.setNegativeButton(android.R.string.no) { dialog, _ ->
                    startCamera()
                    dialog.dismiss()
                }
                // Show the popup because we are 80% certain the item is recyclable
                builder.show()
            }
        }
    }

    private fun recycleItem(signedInUser: User) {
        signedInUser.changePoints(1)
        // Save user and save list of users.
        updateActiveUser(signedInUser)

    }

    private fun updateActiveUser(activeUser: User) {
        // Update the user's properties after they recycled.
        val users = getUsers()
        for (user in users) {
            if (user.email == activeUser.email) {
                user.points = activeUser.points
                updateUser(activeUser)
                updateUsers(users)
                return
            }
        }
    }

    private fun getUsers(): MutableList<User> {
        // Get the list of all users.
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            sharedPrefFile,
            Context.MODE_PRIVATE
        )
        val userJson = sharedPreferences.getString("users_key", "{}")
        return Gson().fromJson(userJson, Array<User>::class.java).toMutableList()
    }

    private fun updateUser(activeUser: User) {
        // Save the active user to shared preferences.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(activeUser)
        editor.putString("active_user_key", usersJson)
        editor.commit()

    }

    private fun isTermOnRecycleList(term: String): Boolean {
        // Check if the list of recyclable items contains the term.
        return recyclableItems.contains(term)
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        // Convert the image for image recognition through tensorflow.
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
        // Convert the image for image recognition through tensorflow.
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

    private fun openProfile() {
        // Check if the user is logged in.
        val user = getSignedInUser()
        val loggedIn = user.getId() != "-1"
        if (loggedIn) {
            // They are logged in so open their profile.
            openProfileConfirmed()
        } else {
            //They are not logged in so open the log in page.
            openLogIn()
        }
    }

    private fun openProfileConfirmed() {
        // Open the users profile screen.
        val intent = Intent(this, ProfileActivity::class.java)
        // start your next activity
        startActivity(intent)
    }

    private fun openLogIn() {
        // Open the log in screen.
        val intent = Intent(this, LogInActivity::class.java)
        // Start your next activity.
        startActivity(intent)
    }

    private fun openMap() {
        // Open the map.
        val intent = Intent(this, MapActivity::class.java)
        // Start your next activity
        startActivity(intent)
    }

    private fun openLeaderboard() {
        // Open the leaderboard.
        val intent = Intent(this, LeaderboardsAndFriendsActivity::class.java)
        // Start your next activity.
        startActivity(intent)
    }

    private fun openRewards() {
        // Open the rewards redemption screen.
        val intent = Intent(this, RewardsActivity::class.java)
        // Start your next activity.
        startActivity(intent)
    }


    private fun openGuide() {
        // Open the guide screen.
        val intent = Intent(this, GuideActivity::class.java)
        // Start your next activity.
        startActivity(intent)
    }

    // Prepare app because permissions were granted.
    private fun allPermissionsGranted(): Boolean {
        for (permission in requiredPermission) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Check if the permissions for the camera were granted or denied.
        if (requestCode == requestPermission) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun updateRewards(rewards: List<Reward>) {
        // Save the rewards to shared preferences.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val rewardsJson = Gson().toJson(rewards)
        Log.e("here", rewardsJson)
        editor.putString("rewards_key", rewardsJson)
        editor.apply()
    }

    private fun updateUsers(users: List<User>) {
        // Update the list of all users.
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val usersJson = Gson().toJson(users)
        editor.putString("users_key", usersJson)
        editor.apply()

    }

    private fun getSignedInUser(): User {
        // If a user is signed in then return them.
        return try {
            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
            val userJson = sharedPreferences.getString("active_user_key", "[]")
            val user: User = Gson().fromJson(userJson, User::class.java)
            user
        } catch (ex: Exception) {
            User("Not", "Found", "HashshlingingSlasher@gmail.com", "Dracula", "-1")
        }
    }

    override fun onDestroy() {
        // Close the image classification model.
        tfLiteClassifier.close()
        super.onDestroy()
    }
}