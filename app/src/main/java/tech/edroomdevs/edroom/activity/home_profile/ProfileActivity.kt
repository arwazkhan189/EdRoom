package tech.edroomdevs.edroom.activity.home_profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import tech.edroomdevs.edroom.BuildConfig
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.activity.login_signin.LoginActivity
import tech.edroomdevs.edroom.databinding.ActivityProfileBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var db: FirebaseFirestore
    private var attendanceMap: HashMap<String, Int> = hashMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //semester
        binding.etProfileSemester.setOnClickListener {
            val itemsSemester = resources.getStringArray(R.array.semester)
            val adapterSemester = ArrayAdapter(this, R.layout.list_design, itemsSemester)
            (binding.etProfileSemester as? AutoCompleteTextView)?.setAdapter(adapterSemester)
        }
        binding.etProfileSemester.addTextChangedListener {
            addAttendanceSubjectMap(
                binding.etProfileDepartment.editableText.toString(),
                binding.etProfileDepartment.editableText.toString()
            )
        }

        // Department
        binding.etProfileDepartment.setOnClickListener {
            val itemsDept = resources.getStringArray(R.array.dept)
            val adapterDept = ArrayAdapter(this, R.layout.list_design, itemsDept)
            (binding.etProfileDepartment as? AutoCompleteTextView)?.setAdapter(adapterDept)
        }

        //update button
        binding.btnProfileUpdate.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Do you want to update your profile?")
                .setPositiveButton("OK") { _, _ ->
                    updateUserData()
                    // reload after update
                    recreate()
                    Toast.makeText(
                        this@ProfileActivity,
                        "Update Successfully!!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                .setNeutralButton("CANCEL") { _, _ ->
                }
                .show()
        }

        //sign out button
        binding.imgProfileSignOut.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Do you want to Sign Out from the App?")
                .setPositiveButton("YES") { _, _ ->
                    signOutUser()
                }
                .setNeutralButton("NO") { _, _ ->
                }
                .show()
        }

    }

    //on resume function
    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            //get user data
            getUserData()
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
    private fun getUserData() {
        val userCurrent = FirebaseAuth.getInstance().currentUser
        val userId = userCurrent?.uid.toString()
        val db = FirebaseFirestore.getInstance()
        db.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                binding.tvProfileFullName.text = "${document.data?.get("fullName")}"
                binding.etProfileDepartment.setText("${document.data?.get("department")}")
                binding.etProfileEmail.setText("${document.data?.get("userEmailId")}")
                binding.etProfileMobileNumber.setText("${document.data?.get("mobileNumber")}")
                //if student then show semester and roll no.
                if ((document.data?.get("rollNumber")).toString() == BuildConfig.teacherKey
                ) {
                    binding.tlProfileSemester.visibility = View.INVISIBLE
                    binding.tlRollNo.visibility = View.INVISIBLE
                } else {
                    binding.tlProfileSemester.visibility = View.VISIBLE
                    binding.tlRollNo.visibility = View.VISIBLE
                    binding.etProfileSemester.setText("${document.data?.get("semester")}")
                    binding.etProfileRollNumber.setText("${document.data?.get("rollNumber")}")
                }
            }.addOnFailureListener {
                binding.tvProfileFullName.text = "Name"
                binding.etProfileDepartment.setText("Department")
                binding.etProfileEmail.setText("abc@gmail.com")
                binding.etProfileMobileNumber.setText("0123456789")
                binding.etProfileSemester.setText("Third")
                binding.etProfileRollNumber.setText("123456789")
            }
    }

    private fun updateUserData() {
        val userCurrent = FirebaseAuth.getInstance().currentUser
        val userId = userCurrent?.uid.toString()
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId).get().addOnSuccessListener { document ->
            if ((document.data?.get("rollNumber")).toString() == BuildConfig.teacherKey
            ) {
                db.collection("Users").document(userId).update(
                    mapOf(
                        "department" to binding.etProfileDepartment.editableText.toString(),
                        "userEmailId" to binding.etProfileEmail.editableText.toString(),
                        "mobileNumber" to binding.etProfileMobileNumber.editableText.toString()
                    )
                )
            } else {
                db.collection("Users").document(userId).update(
                    mapOf(
                        "department" to binding.etProfileDepartment.editableText.toString(),
                        "userEmailId" to binding.etProfileEmail.editableText.toString(),
                        "mobileNumber" to binding.etProfileMobileNumber.editableText.toString(),
                        "semester" to binding.etProfileSemester.editableText.toString(),
                        "rollNumber" to binding.etProfileRollNumber.editableText.toString(),
                        "attendanceMap" to attendanceMap
                    )
                )
            }
        }
    }

    private fun signOutUser() {
        Firebase.auth.signOut()
        Toast.makeText(this@ProfileActivity, "Sign Out Successfully...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    // add Attendance Map
    private fun addAttendanceSubjectMap(branch: String, semester: String) {
        db = FirebaseFirestore.getInstance()
        //first clear the list then store all the subject name in a list
        attendanceMap.clear()
        db.collection("Subjects").document(branch).get().addOnSuccessListener { results ->
            if (results.get(semester) != null) {
                val subjects: List<*> = results.get(semester) as List<*>
                if (subjects.isNotEmpty())
                    subjects.forEach { subject ->
                        attendanceMap[subject.toString()] = 0
                    }
            }
        }
    }

}