package tech.edroomdevs.edroom.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import tech.edroomdevs.edroom.databinding.ActivityRegisterBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignUp.setOnClickListener {
            if (binding.etRegisterEmail.text.toString() == "") {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please enter email.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.etRegisterPassword.text.toString() == "") {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please enter password.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!(binding.radioTeacher.isChecked || binding.radioStudent.isChecked)) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Please Choose Teacher/Student...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val email: String = binding.etRegisterEmail.text.toString().trim { it <= ' ' }
                val password: String =
                    binding.etRegisterPassword.text.toString().trim { it <= ' ' }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            firebaseUser.sendEmailVerification()
                                .addOnCompleteListener { task1 ->
                                    if (task1.isSuccessful) {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Email verification sent successfully.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            //checking user is teacher or student
                            val youAre: String = if (binding.radioStudent.isChecked)
                                "student"
                            else
                                "teacher"

                            if (youAre == "student") {
                                val intent = Intent(
                                    this@RegisterActivity,
                                    AddStudentDetailsActivity::class.java
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                intent.putExtra("password", password)
                                startActivity(intent)
                                finish()
                            } else if (youAre == "teacher") {
                                val intent =
                                    Intent(
                                        this@RegisterActivity,
                                        AddTeacherDetailsActivity::class.java
                                    )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                intent.putExtra("password", password)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                this@RegisterActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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

}