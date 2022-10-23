package com.example.parsegram

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class PostAdapter(val context: Context, val posts: List<Post>) : RecyclerView.Adapter<PostAdapter
.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts.get(position)
        holder.bind(post)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        // Specify the layout file to use for this item

        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent,false)
        return ViewHolder(view);
    }


    override fun getItemCount(): Int {
        return posts.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvUsername : TextView
        val ivImage : ImageView
        val tvDescription : TextView
        val ivProfilePicture : ImageView
        val tvTime : TextView

        init{
            tvUsername = itemView.findViewById(R.id.tvUsername)
            ivImage = itemView.findViewById(R.id.ivImage)
            tvDescription = itemView.findViewById(R.id.tvDescription)
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture)
            tvTime = itemView.findViewById(R.id.tvTime)
        }

        fun bind(post: Post){
            tvDescription.text = post.getDescription()
            tvUsername.text = post.getUser()?.username

            //TODO: pp use Time Formatter
            tvTime.text = TimeFormatter.getTimeDifference(post.createdAt.toString())

            // Populate image
            Glide.with(itemView.context).load(post.getImage()?.url).into(ivImage)
        }
    }

}