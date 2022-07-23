package tech.edroomdevs.edroom.activity.noticeboard

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import tech.edroomdevs.edroom.BuildConfig
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.databinding.ActivityNoticeDetailsBinding
import tech.edroomdevs.edroom.util.ConnectionManager
import tech.edroomdevs.edroom.util.FullImageView


class NoticeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeDetailsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private var downloadId: Long = 0
    private val storage = Firebase.storage("gs://edroom-146bd.appspot.com/")
    private val storageRef = storage.reference

    //broadcast receiver to receive successfully download message
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent?) {
            val id: Long? = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action: String = p1?.action.toString()
            if (id == downloadId && DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                Toast.makeText(
                    applicationContext,
                    "Notice downloaded successfully...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //sharedPreferences initialization
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        //notice content setup
        Picasso.get().load(intent.getStringExtra("noticeImageUrl")).error(R.drawable.notice_image)
            .into(binding.imgNoticeImage)
        binding.tvNoticeTitle.text = intent.getStringExtra("noticeTitle")
        binding.tvNoticeDesc.text = intent.getStringExtra("noticeDescription")

        //show full image when click on it
        binding.imgNoticeImage.setOnClickListener {
            FullImageView(this).showFullImageView(Uri.parse(intent.getStringExtra("noticeImageUrl")))
        }

        //notice download button
        binding.btnDownloadNotice.setOnClickListener {
            Toast.makeText(this@NoticeDetailsActivity, "Downloading Started...", Toast.LENGTH_SHORT)
                .show()
            downloadNotice(
                Uri.parse(intent.getStringExtra("noticeImageUrl")),
                intent.getStringExtra("noticeTitle").toString()
            )
        }
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //notice share button
        binding.btnShareNotice.setOnClickListener {
            shareNotice()
        }

        //notice delete button only for teacher
        if (sharedPreferences.getString("rollNumber", "") == BuildConfig.teacherKey) {
            binding.btnDeleteNotice.visibility = View.VISIBLE
        }
        binding.btnDeleteNotice.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Do you want to delete this Notice?")
                .setPositiveButton("OK") { _, _ ->
                    deleteNotice(intent.getStringExtra("noticeId")!!)
                }
                .setNeutralButton("CANCEL") { _, _ ->
                }
                .show()
        }

    }

    //on resume function
    override fun onResume() {
        if (!(ConnectionManager().checkConnectivity(this))) {
            checkInternet()
        }
        super.onResume()
    }

    // internet check function
    private fun checkInternet() {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
            startActivity(settingsIntent)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this)
        }
        dialog.create()
        dialog.show()
    }

    //download Notice function and broadcast receiver
    private fun downloadNotice(link: Uri, title: String) {
        val request: DownloadManager.Request =
            DownloadManager.Request(link)
                .setTitle(title)
                .setDescription("$title downloading...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setMimeType("image/jpeg")
                .setAllowedOverRoaming(false)

        val downloadManger: DownloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManger.enqueue(request)
    }

    //share Notice
    private fun shareNotice() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey 👋, Checkout this Notice :\n\n${intent.getStringExtra("noticeImageUrl")}\n\nBy: ${
                intent.getStringExtra(
                    "noticeByTeacher"
                )
            } | ${intent.getStringExtra("noticeDate")}"
        )
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, "Share To:"))
    }

    //delete notice function
    private fun deleteNotice(noticeId: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("Notices").document(noticeId)
            .delete()
            .addOnSuccessListener {
                //delete image from firestore also
                storageRef.child("Notices/${intent.getStringExtra("noticeImageTitle")}").delete()
                Toast.makeText(
                    this@NoticeDetailsActivity,
                    "Notice successfully deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@NoticeDetailsActivity,
                    "Error deleting Notice...",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

}