package tech.edroomdevs.edroom.activity.attendance

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.edroomdevs.edroom.databinding.ActivityAttendanceStudentBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class AttendanceStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*binding.tvPercent.text = ""

        binding.tvSubject1.text = ""
        binding.tvSubject2.text = ""
        binding.tvSubject3.text = ""
        binding.tvSubject4.text = ""
        binding.tvSubject5.text = ""

        binding.tvSubject1Percent.text = ""
        binding.tvSubject2Percent.text = ""
        binding.tvSubject3Percent.text = ""
        binding.tvSubject4Percent.text = ""
        binding.tvSubject5Percent.text = ""

        binding.tvRecentSubject.text = ""
        binding.tvRecentTeacher.text = ""*/
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