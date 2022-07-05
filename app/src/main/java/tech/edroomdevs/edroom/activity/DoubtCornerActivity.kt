package tech.edroomdevs.edroom.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.Query
import tech.edroomdevs.edroom.adapter.DoubtRecyclerAdapter
import tech.edroomdevs.edroom.daos.DoubtDao
import tech.edroomdevs.edroom.databinding.ActivityDoubtCornerBinding
import tech.edroomdevs.edroom.model.Doubt
import tech.edroomdevs.edroom.util.ConnectionManager

class DoubtCornerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoubtCornerBinding
    private lateinit var doubtRecyclerAdapter: DoubtRecyclerAdapter
    private lateinit var doubtDao: DoubtDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoubtCornerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddDoubt.setOnClickListener {
            val intent = Intent(this@DoubtCornerActivity, AddDoubtActivity::class.java)
            startActivity(intent)
        }
        setUpDoubtRecyclerView()
    }

    private fun setUpDoubtRecyclerView() {
        doubtDao = DoubtDao()
        val doubtCollection = doubtDao.doubtCollection
        val query = doubtCollection.orderBy("doubtTime", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Doubt>().setQuery(query, Doubt::class.java).build()

        doubtRecyclerAdapter = DoubtRecyclerAdapter(recyclerViewOptions, this)

        binding.recyclerViewDoubts.adapter = doubtRecyclerAdapter
        binding.recyclerViewDoubts.layoutManager = LinearLayoutManager(this)
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

    override fun onStart() {
        super.onStart()
        doubtRecyclerAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        doubtRecyclerAdapter.stopListening()
    }

}