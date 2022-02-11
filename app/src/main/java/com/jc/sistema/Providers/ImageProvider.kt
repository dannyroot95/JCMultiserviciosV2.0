package com.jc.sistema.Providers

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jc.sistema.UI.Menu.ui.products.AddProductActivity
import com.jc.sistema.UI.Menu.ui.products.EditProductActivity
import com.jc.sistema.Utils.Constants

class ImageProvider {


    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is EditProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is AddProductActivity -> {
                        Toast.makeText(activity,"Error al guardar imagen en servidor!", Toast.LENGTH_SHORT).show()
                        activity.hideDialog()
                    }
                    is EditProductActivity -> {
                        Toast.makeText(activity,"Error al guardar imagen en servidor!", Toast.LENGTH_SHORT).show()
                        activity.hideDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }


}