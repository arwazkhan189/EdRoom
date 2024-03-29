package tech.edroomdevs.edroom.activity.noticeboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.*
import tech.edroomdevs.edroom.BuildConfig
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.adapter.NoticeRecyclerAdapter
import tech.edroomdevs.edroom.databinding.ActivityNoticeBoardBinding
import tech.edroomdevs.edroom.model.Notice
import tech.edroomdevs.edroom.util.ConnectionManager

class NoticeBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeBoardBinding
    private lateinit var recyclerNoticeAdapter: NoticeRecyclerAdapter
    private lateinit var noticeList: ArrayList<Notice>
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noticeList = arrayListOf()
        binding.recyclerViewNotice.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotice.setHasFixedSize(true)
        recyclerNoticeAdapter = NoticeRecyclerAdapter(this, noticeList)
        binding.recyclerViewNotice.adapter = recyclerNoticeAdapter

        //sharedPreferences initialization
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        //showing add Notice floating button to teachers only
        if (sharedPreferences.getString("rollNumber", "") == BuildConfig.teacherKey) {
            binding.btnAddNotice.visibility = View.VISIBLE
            binding.btnAddNotice.setOnClickListener {
                val intent = Intent(this@NoticeBoardActivity, AddNoticeActivity::class.java)
                startActivity(intent)
            }
        }

    }

    //on resume function
    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            if (noticeList.isEmpty())
                showData()
        } else {
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

    private fun showData() {
        db = FirebaseFirestore.getInstance()
        db.collection("Notices")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred!!! ",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    for (NoticesData: DocumentChange in value?.documentChanges!!) {
                        if (NoticesData.type == DocumentChange.Type.ADDED) {
                            noticeList.add(NoticesData.document.toObject(Notice::class.java))
                            noticeList.sortByDescending { it.noticeId }
                        }
                    }
                    recyclerNoticeAdapter.notifyDataSetChanged()
                }
            })
    }

    // on back press go to Home Activity
    override fun onBackPressed() {
        finish()
    }

}