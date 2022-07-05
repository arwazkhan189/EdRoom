package tech.edroomdevs.edroom.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.edroomdevs.edroom.databinding.ActivityDoubtDetailsBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class DoubtDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoubtDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoubtDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDoubtTitle.text = intent.getStringExtra("doubtTitle")
        binding.tvDoubtCreatedBy.text = intent.getStringExtra("doubtCreatedBy")
        binding.tvDoubtTime.text = intent.getStringExtra("doubtTime")
        binding.tvDoubtSubject.text = intent.getStringExtra("doubtSubject")
        binding.tvDoubtChapter.text = intent.getStringExtra("doubtChapter")
        binding.tvDoubtDesc.text = intent.getStringExtra("doubtDesc")
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

}