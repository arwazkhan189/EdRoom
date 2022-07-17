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
import tech.edroomdevs.edroom.daos.AnswerDao
import tech.edroomdevs.edroom.databinding.ActivityAddDoubtAnswerBinding
import tech.edroomdevs.edroom.util.ConnectionManager
import tech.edroomdevs.edroom.util.ImageCompression
import java.io.File


class AddAnswerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDoubtAnswerBinding
    private val pickImageCode = 1
    private var imageUri: Uri? = null
    private val storage = Firebase.storage("gs://edroom-146bd.appspot.com/")
    private val storageRef = storage.reference
    private var metadata = storageMetadata {
        contentType = "image/jpg"
    }
    private var storageImageUrl: String = ""
    private var answerImageTitle: String = "Please select an Image…"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDoubtAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        //select image button
        binding.tvAnswerImagePickup.setOnClickListener {
            selectAnswerImage()
        }

        //image upload button
        binding.btnUploadImage.setOnClickListener {
            if (binding.tvAnswerImagePickup.text == resources.getString(R.string.please_select_an_image))
                Toast.makeText(
                    this@AddAnswerActivity,
                    "Please select an image first...",
                    Toast.LENGTH_SHORT
                ).show()
            else
                uploadAnswerImage()
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
        if (binding.etAnswer.text.toString() == "") {
            Toast.makeText(
                this@AddAnswerActivity,
                "Please enter your answer.",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.tvPressUploadButton.visibility == View.VISIBLE) {
            Toast.makeText(
                this@AddAnswerActivity,
                "Please upload the image first...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            uploadAnswerDetails()
        }
    }

    //select image function
    private fun selectAnswerImage() {
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
            binding.tvAnswerImagePickup.text =
                (imageUri.toString().split("%2F", "/").last())
            binding.tvPressUploadButton.visibility = View.VISIBLE
        }
    }

    //upload answer image function and return url
    private fun uploadAnswerImage() {
        //showing progress bar when uploading started
        binding.plUploadImage.visibility = View.VISIBLE
        binding.tvPressUploadButton.visibility = View.INVISIBLE
        //taking answerByName title and date to rename image as answerByName_Date
        val answerByName = sharedPreferences.getString("fullName", "Name")!!
        val currentTime = System.currentTimeMillis()
        answerImageTitle = "${answerByName}${currentTime}".replace(" ", "")

        //firebase storage reference and upload task
        val storageReference = storageRef.child("Answers/$answerImageTitle")
        val uploadTask = storageReference.putFile(imageUri!!, metadata)

        //this is used to return the url after uploading
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Toast.makeText(
                    this@AddAnswerActivity,
                    "Uploading Failed...",
                    Toast.LENGTH_SHORT
                ).show()
            }
            storageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                storageImageUrl = downloadUri.toString()
                binding.plUploadImage.visibility = View.INVISIBLE
                Toast.makeText(
                    this@AddAnswerActivity,
                    "Image Uploaded Successfully...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@AddAnswerActivity,
                    "Uploading Failed...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    //upload doubt data to firestore
    private fun uploadAnswerDetails() {
        val answerTime = System.currentTimeMillis()
        val answerByName = sharedPreferences.getString("fullName", "Name")!!
        val answerId = "${answerByName.replace(" ", "")}${answerTime}"
        val doubtId = intent.getStringExtra("doubtId")!!
        val answeredUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        AnswerDao().addAnswerByUser(
            answerId,
            answerByName,
            binding.etAnswer.editableText.toString(),
            storageImageUrl,
            answerTime,
            answerImageTitle,
            doubtId,
            answeredUserId
        )
        finish()
        Toast.makeText(
            this@AddAnswerActivity,
            "Thank you 🙂",
            Toast.LENGTH_SHORT
        ).show()
    }

}

