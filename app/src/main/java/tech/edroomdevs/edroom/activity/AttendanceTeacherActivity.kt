package tech.edroomdevs.edroom.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.databinding.ActivityAttendanceTeacherBinding
import tech.edroomdevs.edroom.util.ConnectionManager
import java.util.*

class AttendanceTeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceTeacherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Department
        val adapterDept = ArrayAdapter(
            applicationContext,
            R.layout.list_design,
            resources.getStringArray(R.array.dept)
        )
        (binding.etAttendanceDept as? AutoCompleteTextView)?.setAdapter(adapterDept)

        //semester
        val adapterSemester = ArrayAdapter(
            applicationContext,
            R.layout.list_design,
            resources.getStringArray(R.array.semester)
        )
        (binding.etAttendanceSemester as? AutoCompleteTextView)?.setAdapter(adapterSemester)


        //date
        binding.etAttendanceDate.setOnClickListener {
            clickDatePicker(binding)
        }

        //new attendance
        binding.btnAttendanceNew.setOnClickListener {
            if (checkForInputAndGoToActivity(binding, "New Attendance...")) {
                val intent =
                    Intent(this@AttendanceTeacherActivity, NewAttendanceActivity::class.java)
                intent.putExtra("branch", binding.etAttendanceDept.editableText.toString())
                intent.putExtra("semester", binding.etAttendanceSemester.editableText.toString())
                intent.putExtra("date", binding.etAttendanceDate.editableText.toString())
                startActivity(intent)
            }
        }

        //edit attendance
        binding.btnAttendanceEdit.setOnClickListener {
            if (checkForInputAndGoToActivity(binding, "Edit Attendance...")) {
                val intent =
                    Intent(this@AttendanceTeacherActivity, EditAttendanceActivity::class.java)
                intent.putExtra("branch", binding.etAttendanceDept.editableText.toString())
                intent.putExtra("semester", binding.etAttendanceSemester.editableText.toString())
                intent.putExtra("date", binding.etAttendanceDate.editableText.toString())
                startActivity(intent)
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

    //date picker function
    private fun clickDatePicker(binding: ActivityAttendanceTeacherBinding) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etAttendanceDate.setText(selectedDate)
            },
            year, month, day,
        )
        dpd.datePicker.maxDate = Date().time
        dpd.show()
    }

    // check for input
    private fun checkForInputAndGoToActivity(
        binding: ActivityAttendanceTeacherBinding,
        text: String
    ): Boolean {
        if (binding.etAttendanceDept.editableText.toString() == "Department") {
            Toast.makeText(
                this@AttendanceTeacherActivity,
                "Please select Department...",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etAttendanceSemester.editableText.toString() == "Semester") {
            Toast.makeText(
                this@AttendanceTeacherActivity,
                "Please select Semester...",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etAttendanceDate.editableText.toString() == "Date") {
            Toast.makeText(
                this@AttendanceTeacherActivity,
                "Please select Date...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@AttendanceTeacherActivity,
                text,
                Toast.LENGTH_SHORT
            ).show()
            return true
        }
        return false
    }

}