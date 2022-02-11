package com.jc.sistema.Utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val TOKEN = "token"
    const val SUPER_USER = "Super-Admin"
    const val ADMIN = "Administrador"
    const val VENDOR = "Vendedor"
    const val USERS = "users"
    const val TYPE_USER = "type_user"
    const val KEY_TYPE_USER = "key_type_user"
    const val KEY_EMAIL = "key_email"
    const val KEY_PASSWORD = "key_password"
    const val IMAGE_PROFILE = "image_profile"
    const val EMAIL = "email"
    const val USERNAME = "username"
    const val PASSWORD = "password"
    const val GALLERY_REQUEST = 1
    const val EXTRA_USERS_DETAILS = "users_details"
    const val ADD_USER_REQUEST_CODE: Int = 121
    const val PRODUCTS = "products"
    const val ORDERS = "orders"
    const val KEY_ADAPTER = "key_adapter"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 2

fun showImageChooser(activity: Activity) {
    // An intent for launching the image selection of phone storage.
    val galleryIntent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    // Launches the image selection of phone storage using the constant code.
    activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
}

/**
 * A function to get the image file extension of the selected image.
 *
 * @param activity Activity reference.
 * @param uri Image file uri.
 */
fun getFileExtension(activity: Activity, uri: Uri?): String? {
    /*
     * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
     *
     * getSingleton(): Get the singleton instance of MimeTypeMap.
     *
     * getExtensionFromMimeType: Return the registered extension for the given MIME type.
     *
     * contentResolver.getType: Return the MIME type of the given content URL.
     */
    return MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
}


}