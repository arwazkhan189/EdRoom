package tech.edroomdevs.edroom.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.databinding.ActivityNoticeDetailsBinding

class NoticeDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoticeDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Picasso.get().load(intent.getStringExtra("image")).error(R.drawable.current_notice)
            .into(binding.imgNoticeImage)

        binding.tvNoticeTitle.text = intent.getStringExtra("title")
        binding.tvNoticeDesc.text = intent.getStringExtra("description")

    }
}