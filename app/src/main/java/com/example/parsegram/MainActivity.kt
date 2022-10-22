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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parsegram.fragments.ComposeFragment
import com.example.parsegram.fragments.FeedFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // This code sets our custom logo in the ActionBar
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

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {

            item ->

            var fragmentToShow : Fragment? = null

            when(item.itemId){

                R.id.action_home -> {

                    fragmentToShow = FeedFragment()

//                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }

                R.id.action_compose -> {
                    fragmentToShow = ComposeFragment()
                }

                R.id.action_profile -> {
                    // TODO Navigate to the profile screen

                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                }
            }

            if (fragmentToShow!=null){

//                Toast.makeText(this, "Fragment is not null", Toast.LENGTH_SHORT).show()
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }

            // returns true to say that we have handled this user interaction
            true
        }


        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home

        // 3. ImageView to show picture


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



    var cameraResultLauncher: ActivityResultLauncher<Intent>? = null


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