package com.example.parsegram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.parsegram.MainActivity
import com.example.parsegram.Post
import com.example.parsegram.R
import com.parse.ParseFile
import com.parse.ParseUser
import org.w3c.dom.Text
import java.io.File
import java.util.*


class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var ivPreview : ImageView
    lateinit var pb : ProgressBar
    lateinit var etDescription: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPreview = view.findViewById(R.id.imageView)
        pb = view.findViewById<ProgressBar>(R.id.pbLoading)
        etDescription = view.findViewById<EditText>(R.id.description)

        // set OnClickListeners and setup logic
        // 4. Button to save and post

        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            // send post to server
            // Without an image, first

            // Get the description

            val etDescription = view.findViewById<EditText>(R.id.description)
            val description = etDescription.text.toString()
            val user = ParseUser.getCurrentUser()

            if(photoFile!=null){
                submitPost(description, user,photoFile!!)
            }else{
                //TODO print error log message
                Toast.makeText(requireContext(), "Picture file could not be loaded", Toast.LENGTH_SHORT).show()
            }


        }

        // 2. button to launch the camera

        view.findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture
            onLaunchCamera()
        }
    }


    private fun submitPost(description: String, user: ParseUser, file: File) {
        // create Post object

        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))

        // on some click or some loading we need to wait for...
        // on some click or some loading we need to wait for...
        pb.visibility = ProgressBar.VISIBLE
// run a background job and once complete

        post.saveInBackground() { exception ->
            if (exception != null) {

                Log.e(MainActivity.TAG, "Error while saving post")
                exception.printStackTrace()


                Toast.makeText(requireContext(), "Error saving post", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Post successfully submitted!", Toast
                    .LENGTH_SHORT)
                    .show()

                Log.i(MainActivity.TAG, "Successfully saved post")
                // Resetting EditText

                etDescription.text.clear()

                // Resetting ImageView

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
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
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
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                MainActivity.TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode== AppCompatActivity.RESULT_OK){
                // we have the photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // resize bitmap
                ivPreview.setImageBitmap(takenImage)
            }
            else{
                // It did not work
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}