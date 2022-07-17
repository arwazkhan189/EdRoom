package tech.edroomdevs.edroom.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.databinding.ActivityAddNoticeBinding
import tech.edroomdevs.edroom.model.Notice
import tech.edroomdevs.edroom.util.ConnectionManager
import tech.edroomdevs.edroom.util.ImageCompression
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddNoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoticeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val pickImageCode = 1
    private var imageUri: Uri? = null
    private val storage = Firebase.storage("gs://edroom-146bd.appspot.com/")
    private val storageRef = storage.reference
    private var metadata = storageMetadata {
        contentType = "image/jpg"
    }
    private var storageImageUrl: String = ""
    private val db = Firebase.firestore
    private lateinit var noticeImageTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)


        //select image button
        binding.tvNoticeImagePickup.setOnClickListener {
            selectNoticeImage()
        }

        //image upload button
        binding.btnUploadImage.setOnClickListener {
            if (binding.tvNoticeImagePickup.text == resources.getString(R.string.please_select_an_image))
                Toast.makeText(
                    this@AddNoticeActivity,
                    "Please select an image first...",
                    Toast.LENGTH_SHORT
                ).show()
            else
                uploadNoticeImage()
        }

        //notice submit  button
        binding.btnNoticeSubmit.setOnClickListener {
            checkInputFunction()
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

    //check input function
    private fun checkInputFunction() {
        if (binding.etNoticeTitle.text.toString() == "") {
            Toast.makeText(
                this@AddNoticeActivity,
                "Please enter Notice Title.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etNoticeDesc.text.toString() == "") {
            Toast.makeText(
                this@AddNoticeActivity,
                "Please enter Notice description.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (storageImageUrl == "") {
            Toast.makeText(
                this@AddNoticeActivity,
                "Please upload the image first...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            uploadNoticeDetails()
        }
    }

    //select image function
    private fun selectNoticeImage() {
        val selectNoticeImageIntent = Intent()
        selectNoticeImageIntent.type = "image/*"
        selectNoticeImageIntent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(selectNoticeImageIntent, "Select an image :"),
            pickImageCode
        )
    }

    //get image uri function
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImageCode) {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "EdRoom"
            )
            imageUri =
                ImageCompression(applicationContext).compress(
                    (data?.data).toString(),
                    file
                ).toUri()
            binding.tvNoticeImagePickup.text =
                (imageUri.toString().split("%2F", "/").last())
        }
    }

    //upload notice image function and return url
    private fun uploadNoticeImage() {
        //showing progress bar when uploading started
        binding.plUploadImage.visibility = View.VISIBLE

        //taking notice title and date to rename image as NoticeTitle_Date
        val noticeTitle = binding.etNoticeTitle.editableText.toString()
        val currentDate = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
        noticeImageTitle = "${noticeTitle}_${currentDate}".replace(" ", "")

        //firebase storage reference and upload task
        val storageReference = storageRef.child("Notices/$noticeImageTitle")
        val uploadTask = storageReference.putFile(imageUri!!, metadata)

        //this is used to return the url after uploading
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this@AddNoticeActivity, "Uploading Failed...", Toast.LENGTH_SHORT)
                    .show()
            }
            storageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                storageImageUrl = downloadUri.toString()
                binding.plUploadImage.visibility = View.INVISIBLE
                Toast.makeText(
                    this@AddNoticeActivity,
                    "Image Uploaded Successfully...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this@AddNoticeActivity, "Uploading Failed...", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    //upload notice data to firestore
    private fun uploadNoticeDetails() {
        val noticeByTeacher = sharedPreferences.getString("fullName", "Name")
        val createdDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val noticeId = "${noticeByTeacher?.replace(" ", "")}${System.currentTimeMillis()}"
        val noticeData = Notice(
            noticeId,
            binding.etNoticeTitle.editableText.toString(),
            createdDate,
            binding.etNoticeDesc.editableText.toString(),
            storageImageUrl,
            noticeByTeacher,
            noticeImageTitle
        )
        db.collection("Notices").document(noticeId)
            .set(noticeData)
            .addOnSuccessListener {
//                val intent = Intent(this@AddNoticeActivity, NoticeBoardActivity::class.java)
//                startActivity(intent)
//                finishAffinity()
                finish()
                Toast.makeText(this@AddNoticeActivity, "Notice Submitted...", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
//                val intent = Intent(this@AddNoticeActivity, NoticeBoardActivity::class.java)
//                startActivity(intent)
//                finishAffinity()
                finish()
                Toast.makeText(this@AddNoticeActivity, "Some Error occurred...", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}