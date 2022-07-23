package tech.edroomdevs.edroom.activity.doubtcorner

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tech.edroomdevs.edroom.adapter.AnswerRecyclerAdapter
import tech.edroomdevs.edroom.daos.AnswerDao
import tech.edroomdevs.edroom.databinding.ActivityDoubtDetailsBinding
import tech.edroomdevs.edroom.model.Answer
import tech.edroomdevs.edroom.util.ConnectionManager
import tech.edroomdevs.edroom.util.FullImageView

class DoubtDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoubtDetailsBinding
    private lateinit var db: FirebaseFirestore
    private val storage = Firebase.storage("gs://edroom-146bd.appspot.com/")
    private val storageRef = storage.reference
    private lateinit var answerRecyclerAdapter: AnswerRecyclerAdapter
    private lateinit var answerDao: AnswerDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoubtDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup answer recycler view
        setUpAnswerRecyclerView()

        binding.tvDoubtTitle.text = intent.getStringExtra("doubtTitle")
        binding.tvDoubtCreatedBy.text = intent.getStringExtra("doubtCreatedBy")
        binding.tvDoubtTime.text = intent.getStringExtra("doubtTime")
        binding.tvDoubtSubject.text = intent.getStringExtra("doubtSubject")
        binding.tvDoubtChapter.text = intent.getStringExtra("doubtChapter")
        binding.tvDoubtDesc.text = intent.getStringExtra("doubtDesc")

        //download doubt image button
        binding.btnSeeImage.setOnClickListener {
            if (intent.getStringExtra("doubtImageUrl") == "") {
                Toast.makeText(
                    this@DoubtDetailsActivity,
                    "No image is attached...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FullImageView(this).showFullImageView(Uri.parse(intent.getStringExtra("doubtImageUrl")))
            }
        }

        //share doubt button
        binding.btnShareDoubt.setOnClickListener {
            shareDoubt()
        }

        //delete doubt button
        binding.btnDeleteDoubt.setOnClickListener {
            //delete iff user created this doubt
            if (FirebaseAuth.getInstance().currentUser?.uid.toString() == intent.getStringExtra("userId")) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Do you want to delete this Doubt?")
                    .setPositiveButton("OK") { _, _ ->
                        deleteDoubt(intent.getStringExtra("doubtId")!!)
                    }
                    .setNeutralButton("CANCEL") { _, _ ->
                    }
                    .show()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Sorry, You Can't delete this doubt...")
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .show()
            }
        }

        //add answer  button
        binding.btnAddDoubtAnswer.setOnClickListener {
            val addAnswerIntent =
                Intent(this@DoubtDetailsActivity, AddAnswerActivity::class.java)
            addAnswerIntent.putExtra("doubtId", intent.getStringExtra("doubtId"))
            startActivity(addAnswerIntent)
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

    //share Doubt
    private fun shareDoubt() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey 👋, Checkout this Doubt :\n\n*Title:* ${intent.getStringExtra("doubtTitle")}\n*Subject:* ${
                intent.getStringExtra(
                    "doubtSubject"
                )
            }\n*Chapter:* ${intent.getStringExtra("doubtChapter")}\n*Description:* ${
                intent.getStringExtra(
                    "doubtDesc"
                )
            }\n*Image:* ${
                intent.getStringExtra(
                    "doubtImageUrl"
                )
            }\n\n*By:* *${
                intent.getStringExtra(
                    "doubtCreatedBy"
                )
            }*"
        )
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, "Share To:"))
    }

    //delete doubt function
    private fun deleteDoubt(doubtId: String) {
        db = FirebaseFirestore.getInstance()

        //first delete all the images of answers
        db.collection("Answers").whereEqualTo("answeredDoubtId", doubtId).get()
            .addOnSuccessListener { documents ->
                for (document in documents)
                    storageRef.child("Answers/${document.data["answerImageTitle"]}").delete()

                //then delete all the of answers doubt
                db.collection("Doubts").document(doubtId).get().addOnSuccessListener { document ->

                    val items: List<*> = document?.get("answersArray") as List<*>
                    if (items.isNotEmpty())
                        items.forEach {
                            db.collection("Answers").document(it.toString()).delete()
                        }

                    //then delete the doubt
                    db.collection("Doubts").document(doubtId)
                        .delete()
                        .addOnSuccessListener {
                            //delete image from firestore also
                            storageRef.child("Doubts/${intent.getStringExtra("doubtImageTitle")}")
                                .delete()
                            Toast.makeText(
                                this@DoubtDetailsActivity,
                                "Doubt successfully deleted!",
                                Toast.LENGTH_SHORT
                            ).show()
//                val intent = Intent(this@DoubtDetailsActivity, DoubtCornerActivity::class.java)
//                startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this@DoubtDetailsActivity,
                                "Error deleting Doubt...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(
                        this@DoubtDetailsActivity,
                        "Error deleting Doubt...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(
                    this@DoubtDetailsActivity,
                    "Error deleting Doubt...",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    //setup recycler view function
    @SuppressLint("NotifyDataSetChanged")
    private fun setUpAnswerRecyclerView() {
        answerDao = AnswerDao()
        val doubtId = intent.getStringExtra("doubtId")
        val answerCollection = answerDao.answerCollection
        val query = answerCollection.whereEqualTo("answeredDoubtId", doubtId)
            .orderBy("answerTime", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Answer>()
                .setQuery(query, Answer::class.java)
                .build()
        answerRecyclerAdapter = AnswerRecyclerAdapter(recyclerViewOptions, this)
        binding.recyclerViewDoubtAnswers.adapter = answerRecyclerAdapter
        binding.recyclerViewDoubtAnswers.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        answerRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        answerRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        answerRecyclerAdapter.stopListening()
    }

}