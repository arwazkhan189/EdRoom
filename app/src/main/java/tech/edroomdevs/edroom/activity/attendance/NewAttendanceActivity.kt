package tech.edroomdevs.edroom.activity.attendance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.adapter.INewAttendanceRecyclerAdapter
import tech.edroomdevs.edroom.adapter.NewAttendanceRecyclerAdapter
import tech.edroomdevs.edroom.daos.AttendanceDbDao
import tech.edroomdevs.edroom.daos.UserDao
import tech.edroomdevs.edroom.databinding.ActivityNewAttendanceBinding
import tech.edroomdevs.edroom.model.User
import tech.edroomdevs.edroom.util.ConnectionManager

class NewAttendanceActivity : AppCompatActivity(), INewAttendanceRecyclerAdapter {

    private lateinit var binding: ActivityNewAttendanceBinding
    private lateinit var newAttendanceRecyclerAdapter: NewAttendanceRecyclerAdapter
    private lateinit var userDao: UserDao
    private lateinit var attendanceDbDao: AttendanceDbDao
    private lateinit var presentStudentRollNumberList: ArrayList<String>
    private lateinit var userIdListPresent: ArrayList<String>
    private lateinit var userIdListAbsent: ArrayList<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        //initialize array
        presentStudentRollNumberList = arrayListOf()
        userIdListPresent = arrayListOf()
        userIdListAbsent = arrayListOf()

        //attendance done button
        binding.btnAttendanceDone.setOnClickListener {
            val totalStudent = newAttendanceRecyclerAdapter.itemCount
            val totalPresent = presentStudentRollNumberList.size
            MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure to record the attendance?")
                .setMessage("Total Student: $totalStudent\nTotal Present: $totalPresent\nTotal Absent: ${totalStudent - totalPresent}")
                .setPositiveButton("YES") { _, _ ->
                    Toast.makeText(
                        this@NewAttendanceActivity,
                        "Attendance Recorded Successfully...",
                        Toast.LENGTH_LONG
                    ).show()
                    attendanceSubmit()
                }
                .setNeutralButton("NO") { _, _ ->
                }
                .show()
        }

        //set up recycler view
        setUpAttendanceRecyclerView()
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

    //setup recycler view function
    @SuppressLint("NotifyDataSetChanged")
    private fun setUpAttendanceRecyclerView() {
        userDao = UserDao()
        val usersCollection = userDao.usersCollection
        val query =
            usersCollection.whereEqualTo(
                "department",
                intent.getStringExtra("branch")
            ).whereEqualTo("semester", intent.getStringExtra("semester"))
                .orderBy("rollNumber", Query.Direction.ASCENDING)

        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
                .build()

        newAttendanceRecyclerAdapter =
            NewAttendanceRecyclerAdapter(recyclerViewOptions, this)
        binding.recyclerViewStudentList.adapter = newAttendanceRecyclerAdapter
        binding.recyclerViewStudentList.layoutManager = LinearLayoutManager(this)
        newAttendanceRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        newAttendanceRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        newAttendanceRecyclerAdapter.stopListening()
    }

    //on back press
    override fun onBackPressed() {
        startActivity(Intent(this@NewAttendanceActivity, AttendanceTeacherActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    //present click
    override fun onPresentClick(id: String, rollNumber: String) {
        if (!presentStudentRollNumberList.contains(rollNumber)) {
            presentStudentRollNumberList.add(rollNumber)
            if (!userIdListPresent.contains(id))
                userIdListPresent.add(id)
        }
    }

    //absent click
    override fun onAbsentClick(id: String, rollNumber: String) {
        if (presentStudentRollNumberList.contains(rollNumber)) {
            presentStudentRollNumberList.remove(rollNumber)
            if (!userIdListAbsent.contains(id))
                userIdListAbsent.add(id)
        }
    }

    //attendance submit function
    private fun attendanceSubmit() {
        addAttendance(
            intent.getStringExtra("subject").toString(),
            intent.getStringExtra("date").toString(),
            presentStudentRollNumberList
        )
        startActivity(Intent(this@NewAttendanceActivity, AttendanceTeacherActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    //add attendance function
    private fun addAttendance(
        subject: String,
        dateList: String,
        presentStudentRollNumberList: ArrayList<String>
    ) {
        attendanceDbDao = AttendanceDbDao()
        attendanceDbDao.addAttendance(subject, dateList, presentStudentRollNumberList, 1)
        attendanceDbDao.incrementAttendanceValue(userIdListPresent, subject, 1)
        attendanceDbDao.incrementAttendanceValue(userIdListAbsent, subject, 0)
        attendanceDbDao.addRecentAttendance(
            intent.getStringExtra("branch").toString(),
            intent.getStringExtra("semester").toString(),
            sharedPreferences.getString("fullName", "").toString(),
            subject
        )
    }

}





