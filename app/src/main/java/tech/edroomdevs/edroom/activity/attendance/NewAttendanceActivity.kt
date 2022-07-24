package tech.edroomdevs.edroom.activity.attendance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.adapter.StudentListRecyclerAdapter
import tech.edroomdevs.edroom.daos.UserDao
import tech.edroomdevs.edroom.databinding.ActivityNewAttendanceBinding
import tech.edroomdevs.edroom.model.User
import tech.edroomdevs.edroom.util.ConnectionManager

class NewAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAttendanceBinding
    private lateinit var studentListRecyclerAdapter: StudentListRecyclerAdapter
    private lateinit var userDao: UserDao
    private lateinit var db: FirebaseFirestore
    private var subjectList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //show subject
        val adapterSubject = ArrayAdapter(
            applicationContext,
            R.layout.list_design,
            subjectList
        )
        (binding.etAttendanceSubject as? AutoCompleteTextView)?.setAdapter(adapterSubject)

        //attendance done button
        binding.btnAttendanceDone.setOnClickListener {
            if (binding.etAttendanceSubject.editableText.toString() == "Subject") {
                Toast.makeText(
                    this@NewAttendanceActivity,
                    "Please choose a Subject...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                attendanceSubmit()
            }
        }

        //set up recycler view
        setUpAttendanceRecyclerView()
    }

    //on resume function
    override fun onResume() {
        if (!(ConnectionManager().checkConnectivity(this))) {
            checkInternet()
        }
        getSubject()
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

    //get subject function
    private fun getSubject() {
        val branch: String = intent.getStringExtra("branch").toString()
        val semester: String = intent.getStringExtra("semester").toString()
        db = FirebaseFirestore.getInstance()
        //store all the subject name in a list
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

    //attendance submit function
    private fun attendanceSubmit() {
        Toast.makeText(
            this@NewAttendanceActivity,
            "Attendance Recorded Successfully...",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onStart() {
        super.onStart()
        studentListRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        studentListRecyclerAdapter.stopListening()
    }

}




