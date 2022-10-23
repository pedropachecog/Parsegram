package com.example.parsegram

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.util.Date

@ParseClassName("Post")
class Post: ParseObject() {
    fun getDescription(): String?{
        return getString(KEY_DESCRIPTION)
    }
    
    fun setDescription(description: String){
        put(KEY_DESCRIPTION, description)
    }
    
    fun getImage(): ParseFile?{
        return getParseFile(KEY_IMAGE)
    }
    
    fun setImage(parseFile: ParseFile){
        put(KEY_IMAGE, parseFile)
    }
//
//    fun getCreatedAt(createdAt: Date){
//        return getCreatedAt(KEY_CREATED_AT)
//    }


    fun getUser(): ParseUser?{
        return getParseUser(KEY_USER)
    }

    fun setUser(User: ParseUser){
        put(KEY_USER, User)
    }


    companion object{
        const val KEY_DESCRIPTION = "description"
        const val KEY_IMAGE = "image"
        const val KEY_USER="user"
        const val KEY_CREATED_AT = "createdAt"
    }
}