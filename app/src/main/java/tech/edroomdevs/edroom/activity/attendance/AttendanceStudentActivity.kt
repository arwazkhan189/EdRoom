package tech.edroomdevs.edroom.activity.attendance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.edroomdevs.edroom.adapter.AttendanceStudentRecyclerAdapter
import tech.edroomdevs.edroom.daos.AttendanceDbDao
import tech.edroomdevs.edroom.databinding.ActivityAttendanceStudentBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class AttendanceStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceStudentBinding
    private lateinit var attendanceStudentRecyclerAdapter: AttendanceStudentRecyclerAdapter
    private lateinit var attendanceDbDao: AttendanceDbDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recent attendance
        binding.tvRecentSubject.text = ""
        binding.tvRecentTeacher.text = ""

    }

    //on resume function
    override fun onResume() {
        if (!(ConnectionManager().checkConnectivity(this))) {
            checkInternet()
        }
        showData()
        totalPercentage()
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


    //setup recycler view function
    @SuppressLint("NotifyDataSetChanged")
    private fun showData() {
        binding.recyclerAttendancePercentRow.layoutManager = LinearLayoutManager(this)
        binding.recyclerAttendancePercentRow.setHasFixedSize(true)
        attendanceStudentRecyclerAdapter =
            AttendanceStudentRecyclerAdapter(
                intent.getStringArrayListExtra("subjectList") as ArrayList<String>,
                intent.getStringArrayListExtra("percentList") as ArrayList<Int>
            )
        binding.recyclerAttendancePercentRow.adapter = attendanceStudentRecyclerAdapter
        attendanceStudentRecyclerAdapter.notifyDataSetChanged()
    }

    private fun totalPercentage() {
        attendanceDbDao = AttendanceDbDao()
        val percentArray = intent.getStringArrayListExtra("percentList") as ArrayList<Int>
        var totalPercent = 0
        for (percent in percentArray)
            totalPercent += percent
        binding.tvTotalAttendancePercent.text = totalPercent.toString()
    }

}