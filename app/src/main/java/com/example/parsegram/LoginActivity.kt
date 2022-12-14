package com.example.parsegram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseUser


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        //Check if user is logged in
        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity()
        }

        findViewById<Button>(R.id.login_button).setOnClickListener {
            val username = findViewById<EditText>(R.id.et_username).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()

            loginUser(username, password)
        }

        findViewById<Button>(R.id.signupBtn).setOnClickListener {
            val username = findViewById<EditText>(R.id.et_username).text.toString()
            val password = findViewById<EditText>(R.id.et_password).text.toString()

            signUpUser(username, password)
        }
    }

    private fun signUpUser(username: String, password: String) {
        // Create the ParseUser
        val user = ParseUser()
        // Set core properties
        user.username = username
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                // Hooray! Let them use the app now.

                // TODO: Navitate user to Main

            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        ParseUser.logInInBackground(
            username, password, ({ user, e ->
                if (user != null) {
                    // Hooray!  The user is logged in.
                    Log.i(TAG, "Successfully logged in user $username")
                    goToMainActivity()
                } else {
                    // Signup failed.  Look at the ParseException to see what happened.
                    Log.e(TAG, "Error logging in $username")
                    e.printStackTrace()
                    Toast.makeText(this@LoginActivity, "Error logging in user", Toast.LENGTH_SHORT).show()
                }
            })
        )
    }

    // TODO move this to profile fragment
    private fun logout(){
        ParseUser.logOut()
    }

    private fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        val TAG = "LoginActivityPg"
    }

}