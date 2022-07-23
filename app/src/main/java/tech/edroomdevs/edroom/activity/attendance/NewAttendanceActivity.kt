package tech.edroomdevs.edroom.activity.attendance

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import tech.edroomdevs.edroom.adapter.StudentListRecyclerAdapter
import tech.edroomdevs.edroom.daos.UserDao
import tech.edroomdevs.edroom.databinding.ActivityNewAttendanceBinding
import tech.edroomdevs.edroom.model.StudentList
import tech.edroomdevs.edroom.util.ConnectionManager

class NewAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAttendanceBinding
    private lateinit var studentListRecyclerAdapter: StudentListRecyclerAdapter
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* binding.tvBranch.text = intent.getStringExtra("branch")
         binding.tvSemester.text = intent.getStringExtra("semester")
         binding.tvDate.text = intent.getStringExtra("date")
 */
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
            usersCollection.whereEqualTo("department", "CSE").whereEqualTo("yearOfStudy", "Third")
                .orderBy("rollNumber", Query.Direction.ASCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<StudentList>().setQuery(query, StudentList::class.java)
                .build()

        studentListRecyclerAdapter = StudentListRecyclerAdapter(recyclerViewOptions, this)
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

}




