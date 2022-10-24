package com.example.parsegram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parsegram.MainActivity
import com.example.parsegram.Post
import com.example.parsegram.PostAdapter
import com.example.parsegram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

open class FeedFragment : Fragment() {



    lateinit var postsRecyclerView : RecyclerView
    lateinit var adapter: PostAdapter
    lateinit var swipeContainer: SwipeRefreshLayout

    val allPosts = arrayListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // We set up our views and listeners

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView)

        swipeContainer = view.findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing feed")
            queryPosts(20)
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        // Steps to populate RecyclerView
        // 1. Create layout for each row in list (item_post.xml)
        // 2. Create data source for each row (the Post class)
        // 3. Create adapter that will bridge data and row layout (PostAdapter)
        // 4. Set adapter on RecyclerView

        adapter = PostAdapter(requireContext(),allPosts)
        postsRecyclerView.adapter = adapter

        // 5. Set layout manager on RecyclerView

        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        queryPosts(20)

    }


    open fun queryPosts(limit: Int) {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        query.include(Post.KEY_USER)
        if (limit>0)
            query.limit = limit

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

    companion object{
        const val TAG = "FeedFragmentpg"
    }

}