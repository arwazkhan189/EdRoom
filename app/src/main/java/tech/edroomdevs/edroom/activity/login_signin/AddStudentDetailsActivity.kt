package tech.edroomdevs.edroom.activity.login_signin

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.activity.home_profile.HomeActivity
import tech.edroomdevs.edroom.daos.UserDao
import tech.edroomdevs.edroom.databinding.ActivityUserInfoBinding
import tech.edroomdevs.edroom.model.User
import tech.edroomdevs.edroom.util.ConnectionManager

class AddStudentDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //Semester
        val itemsSemester = resources.getStringArray(R.array.semester)
        val adapterSemester = ArrayAdapter(this, R.layout.list_design, itemsSemester)
        (binding.etRegisterSemester as? AutoCompleteTextView)?.setAdapter(adapterSemester)

        // Department
        val itemsDept = resources.getStringArray(R.array.dept)
        val adapterDept = ArrayAdapter(this, R.layout.list_design, itemsDept)
        (binding.etRegisterDept as? AutoCompleteTextView)?.setAdapter(adapterDept)

        binding.btnRegisterSubmit.setOnClickListener {
            if (binding.etRegisterFullName.text.toString() == "") {
                Toast.makeText(
                    this@AddStudentDetailsActivity,
                    "Please enter your full name.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.etRegisterDept.editableText.toString() == "Department") {
                Toast.makeText(
                    this@AddStudentDetailsActivity,
                    "Please select your Department...",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.etRegisterSemester.editableText.toString() == "Semester") {
                Toast.makeText(
                    this@AddStudentDetailsActivity,
                    "Please select your semester...",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.etRegisterRollNo.text.toString() == "") {
                Toast.makeText(
                    this@AddStudentDetailsActivity,
                    "Please enter your roll no.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.etRegisterMobileNo.text.toString() == "") {
                Toast.makeText(
                    this@AddStudentDetailsActivity,
                    "Please enter your mobile number.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                checkUserVerification()
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

    private fun checkUserVerification() {
        val emailId = intent.getStringExtra("email_id")
        val password = intent.getStringExtra("password")
        auth.signInWithEmailAndPassword(emailId!!, password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user!!.isEmailVerified) {
                        Toast.makeText(
                            this@AddStudentDetailsActivity,
                            "You are Login successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@AddStudentDetailsActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        val userInfo =
                            User(
                                user.uid,
                                binding.etRegisterFullName.editableText.toString(),
                                binding.etRegisterDept.editableText.toString(),
                                binding.etRegisterSemester.editableText.toString(),
                                binding.etRegisterRollNo.editableText.toString(),
                                binding.etRegisterMobileNo.editableText.toString(),
                                emailId.toString()
                            )
                        val usersDao = UserDao()
                        usersDao.addUser(userInfo)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddStudentDetailsActivity,
                            "Verify your email first...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@AddStudentDetailsActivity,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onBackPressed() {
        Toast.makeText(
            this@AddStudentDetailsActivity,
            "Back button is disabled",
            Toast.LENGTH_SHORT
        ).show()
    }

}