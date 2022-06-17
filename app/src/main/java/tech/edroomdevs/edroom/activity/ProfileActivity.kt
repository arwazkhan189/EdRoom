package tech.edroomdevs.edroom.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import tech.edroomdevs.edroom.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get User data
        getUserData()

        //update button
        binding.btnProfileUpdate.setOnClickListener {
            updateUserData()
            // reload after update
            recreate()
            Toast.makeText(this@ProfileActivity, "Update Successfully!!!", Toast.LENGTH_SHORT)
                .show()
        }

        //sign out button
        binding.imgProfileSignOut.setOnClickListener {
            signOutUser()
        }

    }

    private fun signOutUser() {
        Firebase.auth.signOut()
        Toast.makeText(this@ProfileActivity, "Sign Out Successfully...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
                binding.etProfileYearOfStudy.setText("${document.data?.get("yearOfStudy")}")
                binding.etProfileRollNumber.setText("${document.data?.get("rollNumber")}")
            }
            .addOnFailureListener {
                binding.tvProfileFullName.text = "Name"
                binding.etProfileDepartment.setText("Department")
                binding.etProfileEmail.setText("abc@gmail.com")
                binding.etProfileMobileNumber.setText("0123456789")
                binding.etProfileYearOfStudy.setText("Third")
                binding.etProfileRollNumber.setText("123456789")
            }
    }

    private fun updateUserData() {
        val userCurrent = FirebaseAuth.getInstance().currentUser
        val userId = userCurrent?.uid.toString()
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId).update(
            mapOf(
                "department" to binding.etProfileDepartment.editableText.toString(),
                "userEmailId" to binding.etProfileEmail.editableText.toString(),
                "mobileNumber" to binding.etProfileMobileNumber.editableText.toString(),
                "yearOfStudy" to binding.etProfileYearOfStudy.editableText.toString(),
                "rollNumber" to binding.etProfileRollNumber.editableText.toString()
            )
        )
    }

}