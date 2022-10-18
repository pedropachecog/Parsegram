package com.example.parsegram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val actionBar = supportActionBar
        actionBar!!.setDisplayOptions(
            actionBar.getDisplayOptions()
                    or ActionBar.DISPLAY_SHOW_CUSTOM
        )
        val imageView = ImageView(actionBar.themedContext)
        imageView.scaleType = ImageView.ScaleType.FIT_START
        imageView.setImageResource(R.drawable.parsegram_logo_white)
        val layoutParams: android.app.ActionBar.LayoutParams = android.app.ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT, (Gravity.LEFT
                    or Gravity.CENTER_VERTICAL)
        )
        layoutParams.rightMargin = 40
        imageView.layoutParams = layoutParams
        actionBar.customView = imageView

        // 1. set description

        val etDescription = findViewById<EditText>(R.id.description)

        // 2. button to launch the camera

        findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture
            onLaunchCamera()
        }

        // 3. ImageView to show picture




        // 4. Button to save and post

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            // send post to server
            // Without an image, first

            // Get the description

            val description = etDescription.text.toString()
            val user = ParseUser.getCurrentUser()

            if(photoFile!=null){
                submitPost(description, user,photoFile!!)
            }else{
                //TODO print error log message
                Toast.makeText(this, "Picture file could not be loaded", Toast.LENGTH_SHORT).show()
            }


        }

//        //Within the onCreate method
//        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                result: ActivityResult ->
//            if (result.resultCode == RESULT_OK) {
//                // by this point we have the camera photo on disk
//                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
//                // RESIZE BITMAP, see section below
//                // Load the taken image into a preview
//                val ivPreview = findViewById<ImageView>(R.id.imageView)
//                ivPreview!!.setImageBitmap(takenImage)
//            } else { // Result was a failure
//                Toast.makeText(this, "Error taking picture", Toast.LENGTH_SHORT).show()
//            }
//        }


        queryPosts()
    }

    private fun submitPost(description: String, user: ParseUser, file: File) {
        // create Post object

        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))

        // on some click or some loading we need to wait for...
        // on some click or some loading we need to wait for...
        val pb = findViewById<ProgressBar>(R.id.pbLoading)
        pb.visibility = ProgressBar.VISIBLE
// run a background job and once complete

        post.saveInBackground() { exception ->
            if (exception != null) {

                Log.e(TAG, "Error while saving post")
                exception.printStackTrace()


                Toast.makeText(this@MainActivity, "Error saving post", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Post successfully submitted!", Toast
                    .LENGTH_SHORT)
                    .show()

                Log.i(TAG, "Successfully saved post")
                // TODO resetting the edittext

                val etDescription = findViewById<EditText>(R.id.description)

                etDescription.text.clear()

                // todo resetting the imageview

                val ivPreview = findViewById<ImageView>(R.id.imageView)
                ivPreview.setImageBitmap(null)

            }
        }

        val delayInMillis: Long = 1000
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                pb.visibility = ProgressBar.INVISIBLE
            }
        }, delayInMillis)

    }
    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    var cameraResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode== RESULT_OK){
                // we have the photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // resize bitmap
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            }
            else{
                // It did not work
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if (posts != null) {
                        for (post in posts) {
                            Log.i(
                                TAG, "Post:" + post.getDescription() + ", username: " + post
                                    .getUser()?.username
                            )
                        }
                    }
                }
            }

        })

    }


    companion object {
        val TAG = "MainActivityPg"
    }
}