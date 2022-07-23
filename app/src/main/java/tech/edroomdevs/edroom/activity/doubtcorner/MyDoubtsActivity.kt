package tech.edroomdevs.edroom.activity.doubtcorner

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import tech.edroomdevs.edroom.adapter.DoubtRecyclerAdapter
import tech.edroomdevs.edroom.daos.DoubtDao
import tech.edroomdevs.edroom.databinding.ActivityMyDoubtsBinding
import tech.edroomdevs.edroom.model.Doubt
import tech.edroomdevs.edroom.util.ConnectionManager

class MyDoubtsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyDoubtsBinding
    private lateinit var doubtRecyclerAdapter: DoubtRecyclerAdapter
    private lateinit var doubtDao: DoubtDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDoubtsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up recycler view
        setUpDoubtRecyclerView()

    }

    //setup recycler view function
    private fun setUpDoubtRecyclerView() {
        doubtDao = DoubtDao()
        val doubtCollection = doubtDao.doubtCollection
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val query = doubtCollection.whereEqualTo("userId", userId)
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

    override fun onBackPressed() {
//        val intent = Intent(this@MyDoubtsActivity, DoubtCornerActivity::class.java)
//        startActivity(intent)
//        finishAffinity()
        finish()
        super.onBackPressed()
    }

}