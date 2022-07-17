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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.daos.DoubtDao
import tech.edroomdevs.edroom.databinding.ActivityAddDoubtBinding
import tech.edroomdevs.edroom.util.ConnectionManager
import tech.edroomdevs.edroom.util.ImageCompression
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddDoubtActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDoubtBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val pickImageCode = 1
    private var imageUri: Uri? = null
    private val storage = Firebase.storage("gs://edroom-146bd.appspot.com/")
    private val storageRef = storage.reference
    private var metadata = storageMetadata {
        contentType = "image/jpg"
    }
    private var storageImageUrl: String = ""
    private var doubtImageTitle: String = "Please select an Image…"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDoubtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        //select image button
        binding.tvDoubtImagePickup.setOnClickListener {
            selectDoubtImage()
        }

        //image upload button
        binding.btnUploadImage.setOnClickListener {
            if (binding.tvDoubtImagePickup.text == resources.getString(R.string.please_select_an_image))
                Toast.makeText(
                    this@AddDoubtActivity,
                    "Please select an image first...",
                    Toast.LENGTH_SHORT
                ).show()
            else
                uploadDoubtImage()
        }

        binding.btnDoubtSubmit.setOnClickListener {
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
        if (binding.etDoubtTitle.text.toString() == "") {
            Toast.makeText(
                this@AddDoubtActivity,
                "Please enter Doubt Title.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etDoubtSubject.text.toString() == "") {
            Toast.makeText(
                this@AddDoubtActivity,
                "Please enter Subject.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etDoubtChapter.text.toString() == "") {
            Toast.makeText(
                this@AddDoubtActivity,
                "Please enter Chapter.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etDoubtDesc.text.toString() == "") {
            Toast.makeText(
                this@AddDoubtActivity,
                "Please enter Doubt description.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.tvPressUploadButton.visibility == View.VISIBLE) {
            Toast.makeText(
                this@AddDoubtActivity,
                "Please upload the image first...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            uploadDoubtDetails()
        }
    }

    //select image function
    private fun selectDoubtImage() {
        val selectImageIntent = Intent()
        selectImageIntent.type = "image/*"
        selectImageIntent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(selectImageIntent, "Select an image :"),
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
            binding.tvDoubtImagePickup.text =
                (imageUri.toString().split("%2F", "/").last())
            binding.tvPressUploadButton.visibility = View.VISIBLE
        }
    }

    //upload doubt image function and return url
    private fun uploadDoubtImage() {
        //showing progress bar when uploading started
        binding.plUploadImage.visibility = View.VISIBLE
        binding.tvPressUploadButton.visibility = View.INVISIBLE
        //taking doubt title and date to rename image as doubtTitle_Date
        val doubtTitle = binding.etDoubtTitle.editableText.toString()
        val currentDate = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
        doubtImageTitle = "${doubtTitle}_${currentDate}".replace(" ", "")

        //firebase storage reference and upload task
        val storageReference = storageRef.child("Doubts/$doubtImageTitle")
        val uploadTask = storageReference.putFile(imageUri!!, metadata)

        //this is used to return the url after uploading
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(this@AddDoubtActivity, "Uploading Failed...", Toast.LENGTH_SHORT)
                    .show()
            }
            storageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                storageImageUrl = downloadUri.toString()
                binding.plUploadImage.visibility = View.INVISIBLE
                Toast.makeText(
                    this@AddDoubtActivity,
                    "Image Uploaded Successfully...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this@AddDoubtActivity, "Uploading Failed...", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    //upload doubt data to firestore
    private fun uploadDoubtDetails() {
        val doubtTime = System.currentTimeMillis()
        val doubtCreatedBy = sharedPreferences.getString("fullName", "Name")!!
        val doubtId = "${doubtCreatedBy}${doubtTime}".replace(" ", "")
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val answersArray: ArrayList<String>? = null
        DoubtDao().addDoubtByUser(
            doubtId,
            binding.etDoubtTitle.editableText.toString(),
            doubtCreatedBy,
            doubtTime,
            binding.etDoubtSubject.editableText.toString(),
            binding.etDoubtChapter.editableText.toString(),
            binding.etDoubtDesc.editableText.toString(),
            storageImageUrl,
            userId,
            doubtImageTitle,
            answersArray
        )
        finish()
        Toast.makeText(
            this@AddDoubtActivity,
            "Your Doubt will be solved soon 🙂",
            Toast.LENGTH_SHORT
        ).show()
    }

}