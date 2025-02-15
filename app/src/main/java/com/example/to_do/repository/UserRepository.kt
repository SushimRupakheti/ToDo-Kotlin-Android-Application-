package com.example.to_do.repository

import android.content.Context
import android.net.Uri
import com.example.to_do.model.UserModel
import com.example.to_do.ui.fragment.ProfileFragment
import com.google.firebase.auth.FirebaseUser

interface UserRepository {


    fun login(email:String, password:String, callback:(Boolean, String)->Unit)

    fun Signup(email:String,password:String,callback:(Boolean,String,String)->Unit)

    fun addUserToDatabase(
        userId: String, userModel: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    )

    fun getCurrentUser(): FirebaseUser?


    fun uploadImage(context: ProfileFragment, imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context, uri: Uri): String?


}