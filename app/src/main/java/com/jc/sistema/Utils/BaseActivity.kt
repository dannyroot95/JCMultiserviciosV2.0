package com.jc.sistema.Utils

import android.app.Dialog
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jc.sistema.R

open class BaseActivity : AppCompatActivity() {

    private lateinit var mDialog: Dialog

    fun showDialog(text: String) {
        mDialog = Dialog(this)
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mDialog.setContentView(R.layout.custom_dialog)
        val title : TextView = mDialog.findViewById(R.id.tv_progress_text)
        val logo : ImageView = mDialog.findViewById(R.id.progress_logo)

        loop(logo)

        title.text = text
        mDialog.setCancelable(false)
        mDialog.setCanceledOnTouchOutside(false)
        //Start the dialog and display it on screen.
        mDialog.show()
    }

    fun hideDialog(){
        mDialog.dismiss()
    }

    private fun loop(logo : ImageView){
        logo.animate().apply {
            duration = 3000
            rotationYBy(360f)
        }.withEndAction {
            logo.animate().apply {
                duration = 3000
                rotationXBy(360f)
            }.start()
        }
    }

}