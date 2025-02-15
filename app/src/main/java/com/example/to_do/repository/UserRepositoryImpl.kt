package com.example.to_do.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.to_do.model.UserModel
import com.example.to_do.ui.fragment.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream
import java.util.concurrent.Executors

class UserRepositoryImpl:UserRepository {
    private lateinit var auth: FirebaseAuth
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref: DatabaseReference = database.reference.child("users")


    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Login success")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun Signup(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registration success", auth.currentUser?.uid.toString())
            } else {
                callback(false, it.exception?.message.toString(), (""))
            }
        }
    }

    override fun addUserToDatabase(
        userId: String,
        userModel: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(userModel).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registration success")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Reset email sent to $email")
            } else {
                callback(false, it.exception?.message.toString())
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dkfhtjmrw",
            "api_key" to "669892313329389",
            "api_secret" to "ajpGroxGpZGXoHJ6zUYsvjhWa40"
        )
    )

    override fun uploadImage(context: ProfileFragment, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.requireContext().contentResolver.openInputStream(imageUri) // ✅ FIXED

                var fileName = getFileNameFromUri(context.requireContext(), imageUri) // ✅ Pass correct context

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )



                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }    }


    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }



}
