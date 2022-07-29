package tech.edroomdevs.edroom.activity.attendance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.adapter.EditAttendanceRecyclerAdapter
import tech.edroomdevs.edroom.adapter.IEditAttendanceRecyclerAdapter
import tech.edroomdevs.edroom.daos.AttendanceDbDao
import tech.edroomdevs.edroom.daos.UserDao
import tech.edroomdevs.edroom.databinding.ActivityEditAttendanceBinding
import tech.edroomdevs.edroom.model.User
import tech.edroomdevs.edroom.util.ConnectionManager

class EditAttendanceActivity : AppCompatActivity(), IEditAttendanceRecyclerAdapter {

    private lateinit var binding: ActivityEditAttendanceBinding
    private lateinit var editAttendanceRecyclerAdapter: EditAttendanceRecyclerAdapter
    private lateinit var userDao: UserDao
    private lateinit var attendanceDbDao: AttendanceDbDao
    private var presentStudentRollNumberList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up recycler view
        setUpAttendanceRecyclerView()

        //button edit done
        binding.btnEditDone.setOnClickListener {
            val totalStudent = editAttendanceRecyclerAdapter.itemCount
            val totalPresent = presentStudentRollNumberList.size
            MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure to record the attendance?")
                .setMessage("Total Student: $totalStudent\nTotal Present: $totalPresent\nTotal Absent: ${totalStudent - totalPresent}")
                .setPositiveButton("YES") { _, _ ->
                    Toast.makeText(
                        this@EditAttendanceActivity,
                        "Attendance Edit Successfully...",
                        Toast.LENGTH_LONG
                    ).show()
                    attendanceEditSubmit()
                }
                .setNeutralButton("NO") { _, _ ->
                }
                .show()
        }
    }

    //on resume function
    override fun onResume() {
        if (!(ConnectionManager().checkConnectivity(this))) {
            checkInternet()
        }
        getPresentStudentRollNumber(
            intent.getStringExtra("subject").toString(), intent.getStringExtra("date").toString()
        )
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
        editAttendanceRecyclerAdapter =
            EditAttendanceRecyclerAdapter(
                recyclerViewOptions,
                intent.getStringArrayListExtra("presentStudentRollNumberList")!!, this
            )
        binding.recyclerViewStudentList.adapter = editAttendanceRecyclerAdapter
        binding.recyclerViewStudentList.layoutManager = LinearLayoutManager(this)
        editAttendanceRecyclerAdapter.notifyDataSetChanged()
    }


    override fun onStart() {
        super.onStart()
        editAttendanceRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        editAttendanceRecyclerAdapter.stopListening()
    }

    //on back press
    override fun onBackPressed() {
        startActivity(Intent(this@EditAttendanceActivity, AttendanceTeacherActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    //if student already present do not mark present if not then mark present
    override fun onPresentClick(id: String, rollNumber: String, fullName: String) {
        if (presentStudentRollNumberList.contains(rollNumber)) {
            Toast.makeText(
                this@EditAttendanceActivity,
                "$fullName Already Present!!!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            presentStudentRollNumberList.add(rollNumber)
        }
    }

    //if student already absent do not mark absent if not then mark absent
    override fun onAbsentClick(id: String, rollNumber: String, fullName: String) {
        if (presentStudentRollNumberList.contains(rollNumber)) {
            presentStudentRollNumberList.remove(rollNumber)
        } else {
            Toast.makeText(
                this@EditAttendanceActivity,
                "$fullName Already Absent!!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //attendance submit function
    private fun attendanceEditSubmit() {
        editAttendance(
            intent.getStringExtra("subject").toString(),
            intent.getStringExtra("date").toString(),
            presentStudentRollNumberList
        )
        startActivity(Intent(this@EditAttendanceActivity, AttendanceTeacherActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    //edit attendance function
    private fun editAttendance(
        subject: String,
        dateList: String,
        presentStudentRollNumberList: ArrayList<String>
    ) {
        attendanceDbDao = AttendanceDbDao()
        attendanceDbDao.addAttendance(subject, dateList, presentStudentRollNumberList)
    }

    //get Present student roll number
    private fun getPresentStudentRollNumber(subject: String, date: String) {
        attendanceDbDao = AttendanceDbDao()
        GlobalScope.launch {
            attendanceDbDao.attendanceCollection.document(subject).get()
                .addOnSuccessListener { dateList ->
                    if (dateList.get(date) != null) {
                        val rollNumber: List<*> = dateList.get(date) as List<*>
                        if (rollNumber.isNotEmpty())
                            rollNumber.forEach {
                                presentStudentRollNumberList.add(it.toString())
                            }
                    }
                }
        }
    }

}