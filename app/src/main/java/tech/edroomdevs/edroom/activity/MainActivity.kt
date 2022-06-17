package tech.edroomdevs.edroom.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        //display name
        displayUserName()

        binding.btnViewProfile.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
        /*
         cardAttendance.setOnClickListener {
             val intent = Intent(this@MainActivity, AttendanceActivity::class.java)
             startActivity(intent)
         }*/

        binding.cardDoubt.setOnClickListener {
            val intent = Intent(this@MainActivity, DoubtCornerActivity::class.java)
            startActivity(intent)
        }

        binding.cardNotice.setOnClickListener {
            val intent = Intent(this@MainActivity, NoticeBoardActivity::class.java)
            startActivity(intent)
        }

        binding.cardResources.setOnClickListener {
            val intent = Intent(this@MainActivity, ResourceActivity::class.java)
            startActivity(intent)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun displayUserName() {
        val userCurrent = FirebaseAuth.getInstance().currentUser
        val userId = userCurrent?.uid.toString()
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                binding.tvUserName.text = "${document.data?.get("fullName")}"
                savePreferences(binding.tvUserName.text.toString())
            }
            .addOnFailureListener {
                binding.tvUserName.text = "Name"
            }
    }

    private fun savePreferences(text: String) {
        sharedPreferences.edit()
            .putString("fullName", text).apply()
    }

}