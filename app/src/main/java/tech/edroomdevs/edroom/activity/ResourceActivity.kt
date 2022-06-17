package tech.edroomdevs.edroom.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import tech.edroomdevs.edroom.adapter.ResourceRecyclerAdapter
import tech.edroomdevs.edroom.databinding.ActivityResourceBinding
import tech.edroomdevs.edroom.model.Resource
import tech.edroomdevs.edroom.util.ConnectionManager

class ResourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResourceBinding
    private lateinit var recyclerResourceAdapter: ResourceRecyclerAdapter
    private lateinit var resourceList: ArrayList<Resource>
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resourceList = arrayListOf()
        binding.recyclerViewResources.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewResources.setHasFixedSize(true)
        recyclerResourceAdapter = ResourceRecyclerAdapter(this, resourceList)
        binding.recyclerViewResources.adapter = recyclerResourceAdapter
    }

    private fun showData() {
        db = FirebaseFirestore.getInstance()
        db.collection("Resources")
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
                    for (ResourcesData: DocumentChange in value?.documentChanges!!) {
                        if (ResourcesData.type == DocumentChange.Type.ADDED) {
                            resourceList.add(ResourcesData.document.toObject(Resource::class.java))
                        }
                    }
                    recyclerResourceAdapter.notifyDataSetChanged()
                }
            })
    }

    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            if (resourceList.isEmpty())
                showData()
        } else {
            checkInternet()
        }
        super.onResume()
    }

    private fun checkInternet() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Error")
        dialog.setMessage("Internet Connection is not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this)
        }
        dialog.create()
        dialog.show()
    }
}