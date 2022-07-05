package tech.edroomdevs.edroom.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private var selectedResourceIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resourceList = arrayListOf()
        //recyclerView setup
        binding.recyclerViewResources.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewResources.setHasFixedSize(true)
        recyclerResourceAdapter = ResourceRecyclerAdapter(this, resourceList)
        binding.recyclerViewResources.adapter = recyclerResourceAdapter

        //add resource request btn
        binding.btnAddResources.setOnClickListener {
            val intent = Intent(this@ResourceActivity, AddResourcesActivity::class.java)
            startActivity(intent)
        }

        //filter resources btn
        binding.btnFilterResources.setOnClickListener {
            filterResources()
        }

        //Search function
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(searchText: Editable?) {
                searchResourceByName(searchText.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    //on resume function
    override fun onResume() {
        if (ConnectionManager().checkConnectivity(this)) {
            if (resourceList.isEmpty())
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

    //show data function
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

    //search function
    private fun searchResourceByName(searchText: String) {
        val filteredList = arrayListOf<Resource>()
        for (item in resourceList) {
            if (item.resourceName?.lowercase()?.contains(searchText.lowercase()) == true
            ) {
                filteredList.add(item)
            }
        }
        if (filteredList.size == 0) {
            binding.rLNotFoundResources.visibility = View.VISIBLE
        } else {
            binding.rLNotFoundResources.visibility = View.INVISIBLE
        }
        recyclerResourceAdapter = ResourceRecyclerAdapter(this, resourceList)
        binding.recyclerViewResources.adapter = recyclerResourceAdapter
        binding.recyclerViewResources.layoutManager = LinearLayoutManager(this)
        recyclerResourceAdapter.filterList(filteredList)
    }

    //filter resources function
    @SuppressLint("NotifyDataSetChanged")
    private fun filterResources() {
        val resourceType = arrayOf("None", "Course", "PDF")
        val filteredList = arrayListOf<Resource>()
        MaterialAlertDialogBuilder(this)
            .setTitle("Filter By?")
            .setSingleChoiceItems(resourceType, selectedResourceIndex) { _, which ->
                // Respond to item chosen
                selectedResourceIndex = which
            }
            .setPositiveButton("OK") { _, _ ->
                when (selectedResourceIndex) {
                    0 -> {
                        for (item in resourceList)
                            filteredList.add(item)
                    }
                    1 -> {
                        for (item in resourceList) {
                            if (item.resourceCategory?.contains("Course") == true)
                                filteredList.add(item)
                        }
                    }
                    2 -> {
                        for (item in resourceList) {
                            if (item.resourceCategory?.contains("PDF") == true)
                                filteredList.add(item)
                        }
                    }
                }
                if (filteredList.size == 0) {
                    Toast.makeText(
                        this@ResourceActivity,
                        "This Category resources is not present...",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    recyclerResourceAdapter.filterList(filteredList)
                    recyclerResourceAdapter.notifyDataSetChanged()
                    Toast.makeText(this@ResourceActivity, "filtering done...", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNeutralButton("CANCEL") { _, _ ->
                //recyclerView setup
                selectedResourceIndex = 0
                binding.recyclerViewResources.layoutManager = LinearLayoutManager(this)
                recyclerResourceAdapter = ResourceRecyclerAdapter(this, resourceList)
                binding.recyclerViewResources.adapter = recyclerResourceAdapter
            }
            .show()
    }

}
