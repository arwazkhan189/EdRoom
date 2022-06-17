package tech.edroomdevs.edroom.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.daos.UserDao
import tech.edroomdevs.edroom.databinding.ActivityUserInfoBinding
import tech.edroomdevs.edroom.model.User

class UserInfo : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //Year of study
        val itemsYear = resources.getStringArray(R.array.year)
        val adapterYear = ArrayAdapter(this, R.layout.list_year, itemsYear)
        (binding.etRegisterYear as? AutoCompleteTextView)?.setAdapter(adapterYear)
        binding.etRegisterYear.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@UserInfo,
                        "Please select your year of study...",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }
            }

        // Department
        val itemsDept = resources.getStringArray(R.array.dept)
        val adapterDept = ArrayAdapter(this, R.layout.list_dept, itemsDept)
        (binding.etRegisterDept as? AutoCompleteTextView)?.setAdapter(adapterDept)
        binding.etRegisterDept.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(
                        this@UserInfo,
                        "Please select your Department...",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }
            }

        binding.btnRegisterSubmit.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etRegisterFullName.text.toString().trim {
                    it <= ' '
                }) -> {
                    Toast.makeText(
                        this@UserInfo,
                        "Please enter your full name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.etRegisterMobileNo.text.toString().trim {
                    it <= ' '
                }) -> {
                    Toast.makeText(
                        this@UserInfo,
                        "Please enter your mobile number.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.etRegisterRollNo.text.toString().trim {
                    it <= ' '
                }) -> {
                    Toast.makeText(
                        this@UserInfo,
                        "Please enter your roll no.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    checkUserVerification()
                }
            }
        }

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
                            this@UserInfo,
                            "You are Login successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@UserInfo, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        val userInfo =
                            User(
                                user.uid,
                                binding.etRegisterFullName.editableText.toString(),
                                binding.etRegisterDept.editableText.toString(),
                                binding.etRegisterYear.editableText.toString(),
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
                            this@UserInfo,
                            "Verify your email first...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this@UserInfo,
                        task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onBackPressed() {
        Toast.makeText(
            this@UserInfo,
            "Back button is disabled",
            Toast.LENGTH_SHORT
        ).show()
    }

}