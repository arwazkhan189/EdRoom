package tech.edroomdevs.edroom.activity.attendance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import tech.edroomdevs.edroom.adapter.AttendanceStudentRecyclerAdapter
import tech.edroomdevs.edroom.databinding.ActivityAttendanceStudentBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class AttendanceStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceStudentBinding
    private lateinit var attendanceStudentRecyclerAdapter: AttendanceStudentRecyclerAdapter
    val db = FirebaseFirestore.getInstance()
    private lateinit var userBranch: String
    private lateinit var userSemester: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //calling total percentage
        totalPercentage()

        //get recent attendance
        getRecentAttendance()
    }

    //on resume function
    override fun onResume() {
        if (!(ConnectionManager().checkConnectivity(this))) {
            checkInternet()
        }
        showData()
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
                intent.getStringArrayListExtra("percentList") as ArrayList<Int>,
                intent.getStringArrayListExtra("totalSubjectClassList") as ArrayList<Int>
            )
        binding.recyclerAttendancePercentRow.adapter = attendanceStudentRecyclerAdapter
        attendanceStudentRecyclerAdapter.notifyDataSetChanged()
    }

    // getting total percentage
    @SuppressLint("SetTextI18n")
    private fun totalPercentage() {
        var totalClass = 0
        var totalPercent = 0
        val percentArray = intent.getStringArrayListExtra("percentList") as ArrayList<Int>
        val totalClassArray =
            intent.getStringArrayListExtra("totalSubjectClassList") as ArrayList<Int>
        for (percent in percentArray)
            totalPercent += percent
        for (i in totalClassArray)
            totalClass += i
        if (totalClass != 0)
            binding.tvTotalAttendancePercent.text = "${(totalPercent / totalClass)} %"
        else
            binding.tvTotalAttendancePercent.text = "...%"
    }

    //getting recent attendance
    @SuppressLint("SetTextI18n")
    private fun getRecentAttendance() {
        val userCurrent = FirebaseAuth.getInstance().currentUser
        val userId = userCurrent?.uid.toString()
        db.collection("Users").document(userId).get().addOnSuccessListener {
            userBranch = it.get("department").toString()
            userSemester = it.get("semester").toString()
            db.collection("RecentAttendance").document(userBranch + userSemester).get()
                .addOnSuccessListener { ref ->
                    if (ref.get("recentTeacher") != null && ref.get("recentSubject") != null) {
                        binding.tvRecentSubject.text = ref.get("recentSubject").toString()
                        binding.tvRecentTeacher.text = "Prof. ${ref.get("recentTeacher")}"
                    }
                }
        }
    }

}