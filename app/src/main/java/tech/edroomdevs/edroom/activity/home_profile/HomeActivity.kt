package tech.edroomdevs.edroom.activity.home_profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import tech.edroomdevs.edroom.BuildConfig
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.activity.attendance.AttendanceStudentActivity
import tech.edroomdevs.edroom.activity.attendance.AttendanceTeacherActivity
import tech.edroomdevs.edroom.activity.doubtcorner.DoubtCornerActivity
import tech.edroomdevs.edroom.activity.noticeboard.NoticeBoardActivity
import tech.edroomdevs.edroom.activity.resources.ResourceActivity
import tech.edroomdevs.edroom.databinding.ActivityHomeBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private var subjectList = arrayListOf<String>()
    private var percentList = arrayListOf<Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)


        binding.btnViewProfile.setOnClickListener {
            val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.cardAttendance.setOnClickListener {
            if (sharedPreferences.getString("rollNumber", "") == BuildConfig.teacherKey) {
                val intent = Intent(this@HomeActivity, AttendanceTeacherActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@HomeActivity, AttendanceStudentActivity::class.java)
                intent.putExtra("subjectList", subjectList)
                intent.putExtra("percentList", percentList)
                startActivity(intent)
            }
        }

        binding.cardDoubt.setOnClickListener {
            val intent = Intent(this@HomeActivity, DoubtCornerActivity::class.java)
            startActivity(intent)
        }

        binding.cardNotice.setOnClickListener {
            val intent = Intent(this@HomeActivity, NoticeBoardActivity::class.java)
            startActivity(intent)
        }

        binding.cardResources.setOnClickListener {
            val intent = Intent(this@HomeActivity, ResourceActivity::class.java)
            startActivity(intent)
        }

    }

    //on resume function
    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            displayUserName()
            collectData()
        } else {
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
                savePreferences(
                    binding.tvUserName.text.toString(),
                    "${document.data?.get("rollNumber")}"
                )
            }
            .addOnFailureListener {
                binding.tvUserName.text = "Name"
            }
    }

    private fun savePreferences(fullName: String, rollNumber: String) {
        sharedPreferences.edit().putString("fullName", fullName).apply()
        sharedPreferences.edit().putString("rollNumber", rollNumber).apply()
    }

    //collect data
    private fun collectData() {
        subjectList.clear()
        percentList.clear()
        db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("Users").document(userId.toString()).get()
            .addOnSuccessListener { attendanceMap ->
                if (attendanceMap.get("attendanceMap") != null) {
                    val tempMap: HashMap<*, *> =
                        attendanceMap.get("attendanceMap") as HashMap<*, *>
                    tempMap.forEach {
                        subjectList.add(it.key.toString())
                        percentList.add(it.value.toString().toInt())
                    }
                }
            }
    }

}