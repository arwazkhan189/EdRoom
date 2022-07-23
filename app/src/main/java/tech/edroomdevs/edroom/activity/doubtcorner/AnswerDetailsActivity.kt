package tech.edroomdevs.edroom.activity.doubtcorner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tech.edroomdevs.edroom.databinding.ActivityAnswerDetailsBinding
import tech.edroomdevs.edroom.util.ConnectionManager
import tech.edroomdevs.edroom.util.FullImageView

class AnswerDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnswerDetailsBinding
    private lateinit var db: FirebaseFirestore
    private val storage = Firebase.storage("gs://edroom-146bd.appspot.com/")
    private val storageRef = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //showing details
        binding.tvAnswerByName.text = intent.getStringExtra("answerByName")
        binding.tvAnswerTime.text = intent.getStringExtra("answerTime")
        binding.tvAnswerDesc.text = intent.getStringExtra("answerDesc")

        //see answer image button
        binding.btnSeeImage.setOnClickListener {
            if (intent.getStringExtra("answerImageUrl") == "") {
                Toast.makeText(
                    this@AnswerDetailsActivity,
                    "No image is attached...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FullImageView(this).showFullImageView(Uri.parse(intent.getStringExtra("answerImageUrl")))
            }
        }

        //delete answer button for answered user
        binding.btnDeleteDoubtAnswer.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser?.uid.toString() == intent.getStringExtra("answeredUserId")) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Do you want to delete this Answer?")
                    .setPositiveButton("OK") { _, _ ->
                        deleteAnswer(
                            intent.getStringExtra("answerId")!!,
                            intent.getStringExtra("answeredDoubtId")!!
                        )
                    }
                    .setNeutralButton("CANCEL") { _, _ ->
                    }
                    .show()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Sorry, You Can't delete this answer...")
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .show()
            }
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

    //delete answer function
    private fun deleteAnswer(answerId: String, answeredDoubtId: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("Answers").document(answerId)
            .delete()
            .addOnSuccessListener {
                //delete image from firestore also
                storageRef.child("Answers/${intent.getStringExtra("answerImageTitle")}").delete()
                //delete also from array
                db.collection("Doubts").document(answeredDoubtId)
                    .update("answersArray", FieldValue.arrayRemove(answerId))
                Toast.makeText(
                    this@AnswerDetailsActivity,
                    "Answer successfully deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@AnswerDetailsActivity,
                    "Error deleting Answer...",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}