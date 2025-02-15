package com.example.to_do.viewmodel

import android.net.Uri
import com.example.to_do.model.UserModel
import com.example.to_do.repository.UserRepository
import com.example.to_do.ui.fragment.ProfileFragment

class UserViewModel(var repo: UserRepository) {
    fun login(email:String,password:String,callback:(Boolean,String)->Unit){
        repo.login(email,password,callback)
    }

    fun Signup(email:String,password:String,callback:(Boolean,String,String)->Unit){
        repo.Signup(email,password,callback)
    }

    fun addUserToDatabase(userId: String, userModel: UserModel, callback: (Boolean, String) -> Unit){
        repo.addUserToDatabase(userId,userModel,callback)
    }
    fun uploadImage(context: ProfileFragment, imageUri: Uri, callback: (String?) -> Unit){
        repo.uploadImage(context, imageUri, callback)
    }




}