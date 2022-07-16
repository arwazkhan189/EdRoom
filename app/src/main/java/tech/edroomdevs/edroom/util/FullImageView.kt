package tech.edroomdevs.edroom.util

import android.app.Activity
import android.content.Intent
import android.net.Uri

class FullImageView(activity: Activity) {
    private var mActivity: Activity = activity

    fun showFullImageView(imageUrl: Uri) {
        val imageIntent = Intent()
        imageIntent.action = Intent.ACTION_VIEW
        imageIntent.setDataAndType(imageUrl, "image/*")
        mActivity.startActivity(imageIntent)
    }
}

