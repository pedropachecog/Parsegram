package com.example.parsegram.fragments

import android.util.Log
import com.example.parsegram.Post
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment : FeedFragment() {


    override fun queryPosts(limit: Int) {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)

        // Only gets posts by currently signed in user

        query.whereEqualTo(Post.KEY_USER,ParseUser.getCurrentUser())

        // Return posts in descending order : newer posts will appear first
        query.addDescendingOrder("createdAt")

        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    Log.e(TAG, "Error fetching posts")
                    e.printStackTrace()
                } else {
                    if (posts != null) {
                        adapter.clear()
                        for (post in posts) {
                            Log.i(
                                TAG, "Post:" + post.getDescription() + ", username: " + post
                                    .getUser()?.username)

                        }
                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()


                        // Stop refreshing
                        swipeContainer.isRefreshing = false
                    }

                }
            }

        })

    }

}