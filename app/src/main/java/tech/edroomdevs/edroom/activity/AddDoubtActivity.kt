package tech.edroomdevs.edroom.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.daos.DoubtDao
import tech.edroomdevs.edroom.databinding.ActivityAddDoubtBinding

class AddDoubtActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDoubtBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDoubtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        binding.btnDoubtSubmit.setOnClickListener {
            addDoubt()
        }

    }

    private fun addDoubt() {
        if (binding.etDoubtTitle.text.toString().isNotEmpty()
            && binding.etDoubtSubject.text.toString().isNotEmpty()
            && binding.etDoubtChapter.text.toString().isNotEmpty()
            && binding.etDoubtDesc.text.toString().isNotEmpty()
        ) {
            val doubtTitle = binding.etDoubtTitle.text.toString()
            val doubtSubject = binding.etDoubtSubject.text.toString()
            val doubtChapter = binding.etDoubtChapter.text.toString()
            val doubtDesc = binding.etDoubtDesc.text.toString()
            val doubtTime = System.currentTimeMillis()
            val doubtCreatedBy = sharedPreferences.getString("fullName", "Name")!!
            DoubtDao().addDoubtByUser(
                doubtTitle,
                doubtCreatedBy,
                doubtTime,
                doubtSubject,
                doubtChapter,
                doubtDesc
            )
            finish()
            Toast.makeText(
                this@AddDoubtActivity,
                "Your Doubt will be solved soon 🙂",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@AddDoubtActivity,
                "Please fill all the details...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}