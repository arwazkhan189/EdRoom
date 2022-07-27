package tech.edroomdevs.edroom.activity.attendance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.adapter.IStudentListRecyclerAdapter
import tech.edroomdevs.edroom.adapter.StudentListRecyclerAdapter
import tech.edroomdevs.edroom.daos.AttendanceDbDao
import tech.edroomdevs.edroom.daos.UserDao
import tech.edroomdevs.edroom.databinding.ActivityNewAttendanceBinding
import tech.edroomdevs.edroom.model.User
import tech.edroomdevs.edroom.util.ConnectionManager

class NewAttendanceActivity : AppCompatActivity(), IStudentListRecyclerAdapter {

    private lateinit var binding: ActivityNewAttendanceBinding
    private lateinit var studentListRecyclerAdapter: StudentListRecyclerAdapter
    private lateinit var userDao: UserDao
    private lateinit var attendanceDbDao: AttendanceDbDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var totalStudent: Int
        var totalPresent: Int

        //attendance done button
        binding.btnAttendanceDone.setOnClickListener {
            totalStudent = studentListRecyclerAdapter.itemCount
            totalPresent = getTotalStudentPresent()
            Log.e("i", totalPresent.toString())
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

        studentListRecyclerAdapter =
            StudentListRecyclerAdapter(recyclerViewOptions, this)
        binding.recyclerViewStudentList.adapter = studentListRecyclerAdapter
        binding.recyclerViewStudentList.layoutManager = LinearLayoutManager(this)
        studentListRecyclerAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        studentListRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        studentListRecyclerAdapter.stopListening()
    }

    //on back press
    override fun onBackPressed() {
        startActivity(Intent(this, AttendanceTeacherActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    //present click
    override fun onPresentClick(id: String, rollNumber: String) {
        attendanceDbDao = AttendanceDbDao()
        attendanceDbDao.markAttendancePresent(
            intent.getStringExtra("subject")!!,
            intent.getStringExtra("date")!!,
            rollNumber
        )
    }

    //absent click
    override fun onAbsentClick(id: String, rollNumber: String) {
        attendanceDbDao = AttendanceDbDao()
        attendanceDbDao.markAttendanceAbsent(
            intent.getStringExtra("subject")!!,
            intent.getStringExtra("date")!!,
            rollNumber
        )
    }

    //attendance submit function
    private fun attendanceSubmit() {
        startActivity(Intent(this, AttendanceTeacherActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    private fun getTotalStudentPresent(): Int {
        var totalPresent = 0
        attendanceDbDao = AttendanceDbDao()
        val attendanceCollection = attendanceDbDao.attendanceCollection
        val subject = intent.getStringExtra("subject")!!
        val date = intent.getStringExtra("date")!!
        attendanceCollection.document(subject).get().addOnSuccessListener { snapshot ->
            if (snapshot.get(date) != null) {
                totalPresent = (snapshot.get(date) as List<*>).size
            }
        }
        return totalPresent
    }

}




