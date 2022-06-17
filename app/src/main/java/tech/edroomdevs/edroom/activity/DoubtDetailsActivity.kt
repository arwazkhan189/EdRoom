package tech.edroomdevs.edroom.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.edroomdevs.edroom.databinding.ActivityDoubtDetailsBinding

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
}