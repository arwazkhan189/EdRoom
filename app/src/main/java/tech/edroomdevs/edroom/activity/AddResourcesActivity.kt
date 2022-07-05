package tech.edroomdevs.edroom.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.databinding.ActivityAddResourcesBinding
import tech.edroomdevs.edroom.util.ConnectionManager

class AddResourcesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddResourcesBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddResourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add/Request Resource form
        binding.webViewAddResources.webViewClient = WebViewClient()
        binding.webViewAddResources.apply {
            loadUrl(resources.getString(R.string.addRequestResourceUrl))
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled = true
        }
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