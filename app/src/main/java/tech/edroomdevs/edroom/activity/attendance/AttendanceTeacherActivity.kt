package tech.edroomdevs.edroom.activity.attendance

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.daos.AttendanceDbDao
import tech.edroomdevs.edroom.databinding.ActivityAttendanceTeacherBinding
import tech.edroomdevs.edroom.util.ConnectionManager
import java.util.*

class AttendanceTeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceTeacherBinding
    private lateinit var db: FirebaseFirestore
    private var subjectList: ArrayList<String> = arrayListOf()
    private lateinit var attendanceDbDao: AttendanceDbDao
    private var attendanceTimes: String = "1"

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
        binding.etAttendanceSemester.addTextChangedListener {
            getSubject(
                binding.etAttendanceDept.editableText.toString(),
                binding.etAttendanceSemester.editableText.toString()
            )
        }

        //subject
        val adapterSubject = ArrayAdapter(
            applicationContext,
            R.layout.list_design,
            subjectList
        )
        (binding.etAttendanceSubject as? AutoCompleteTextView)?.setAdapter(adapterSubject)

        //attendance date
        binding.etAttendanceDate.setOnClickListener {
            clickDatePicker(binding)
        }

        //no of times attendance
        binding.etAttendanceTimes.minValue = 1
        binding.etAttendanceTimes.maxValue = 5
        binding.etAttendanceTimes.value = 1
        binding.etAttendanceTimes.wrapSelectorWheel = true
        binding.etAttendanceTimes.setOnValueChangedListener { _, _, new ->
            attendanceTimes = new.toString()
        }

        //new attendance
        binding.btnAttendanceNew.setOnClickListener {
            if (checkForInputAndGoToActivity(binding)) {
                val branch = binding.etAttendanceDept.editableText.toString()
                val semester = binding.etAttendanceSemester.editableText.toString()
                val subject = binding.etAttendanceSubject.editableText.toString()
                val date = binding.etAttendanceDate.editableText.toString()

                val intent =
                    Intent(this@AttendanceTeacherActivity, NewAttendanceActivity::class.java)
                intent.putExtra("branch", branch)
                intent.putExtra("semester", semester)
                intent.putExtra("subject", subject)
                MaterialAlertDialogBuilder(this)
                    .setTitle("Confirm New Attendance?")
                    .setMessage("Branch: $branch\nSemester: $semester\nSubject: $subject\nDate: $date\nClass: $attendanceTimes")
                    .setPositiveButton("YES") { _, _ ->
                        if (attendanceTimes == "1") {
                            intent.putExtra("date", date.replace("/", ""))
                            createAttendanceArray(subject, date.replace("/", ""))
                        } else {
                            intent.putExtra("date", date.replace("/", "") + attendanceTimes)
                            createAttendanceArray(subject, date.replace("/", "") + attendanceTimes)
                        }
                        startActivity(intent)
                    }
                    .setNeutralButton("NO") { _, _ ->
                    }
                    .show()
            }
        }

        //edit attendance
        binding.btnAttendanceEdit.setOnClickListener {
            if (checkForInputAndGoToActivity(binding)) {
                val branch = binding.etAttendanceDept.editableText.toString()
                val semester = binding.etAttendanceSemester.editableText.toString()
                val subject = binding.etAttendanceSubject.editableText.toString()
                val date = binding.etAttendanceDate.editableText.toString()

                val intent =
                    Intent(this@AttendanceTeacherActivity, EditAttendanceActivity::class.java)
                intent.putExtra("branch", branch)
                intent.putExtra("semester", semester)
                intent.putExtra("subject", subject)
                MaterialAlertDialogBuilder(this)
                    .setTitle("Confirm Edit Attendance?")
                    .setMessage("Branch: $branch\nSemester: $semester\nSubject: $subject\nDate: $date\nClass: $attendanceTimes")
                    .setPositiveButton("YES") { _, _ ->
                        if (attendanceTimes == "1") {
                            intent.putExtra("date", date.replace("/", ""))
                            createAttendanceArray(subject, date.replace("/", ""))
                        } else {
                            intent.putExtra("date", date.replace("/", "") + attendanceTimes)
                            createAttendanceArray(subject, date.replace("/", "") + attendanceTimes)
                        }
                        startActivity(intent)
                    }
                    .setNeutralButton("NO") { _, _ ->
                    }
                    .show()
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

    //get subject function
    private fun getSubject(branch: String, semester: String) {
        db = FirebaseFirestore.getInstance()
        //first clear the list then store all the subject name in a list
        subjectList.clear()
        db.collection("Subjects").document(branch).get().addOnSuccessListener { results ->
            if (results.get(semester) != null) {
                val subjects: List<*> = results.get(semester) as List<*>
                if (subjects.isNotEmpty())
                    subjects.forEach { subject ->
                        subjectList.add(subject.toString())
                    }
            }
        }
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
    private fun checkForInputAndGoToActivity(binding: ActivityAttendanceTeacherBinding): Boolean {
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
        } else if (binding.etAttendanceSubject.editableText.toString() == "Subject") {
            Toast.makeText(
                this@AttendanceTeacherActivity,
                "Please select Subject...",
                Toast.LENGTH_SHORT
            ).show()
        } else if (binding.etAttendanceDate.editableText.toString() == "Date") {
            Toast.makeText(
                this@AttendanceTeacherActivity,
                "Please select Date...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            return true
        }
        return false
    }

    //create attendance array
    private fun createAttendanceArray(subject: String, dateList: String) {
        attendanceDbDao = AttendanceDbDao()
        attendanceDbDao.addAttendance(subject, dateList)
    }

}
