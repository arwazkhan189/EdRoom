package tech.edroomdevs.edroom.activity.splash_onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.activity.login_signin.LoginActivity
import tech.edroomdevs.edroom.activity.splash_onboarding.carouselview.CarouselPage
import tech.edroomdevs.edroom.activity.splash_onboarding.carouselview.CarouselPager
import tech.edroomdevs.edroom.activity.splash_onboarding.carouselview.ZoomOutPageTransformer

class OnboardingScreen : AppCompatActivity(), CarouselPager.CarouselListener {

    private val carouselPages = listOf(
        CarouselPage(
            R.drawable.finalattendance,
            R.string.onBoarding1_title,
            R.string.onBoarding1_text
        ),
        CarouselPage(R.drawable.doubtfinal, R.string.onBoarding2_title, R.string.onBoarding2_text),
        CarouselPage(R.drawable.resource, R.string.onBoarding3_title, R.string.onBoarding3_text)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        findViewById<CarouselPager>(R.id.carouselPager).setUpCarousel(
            this,
            carouselPages
        ).setUpPageTransformer(ZoomOutPageTransformer()).setUpCarouselListener(this)
    }

    override fun onCarouselFinished(skipped: Boolean) {
        startActivity(Intent(this@OnboardingScreen, LoginActivity::class.java))
        finishAffinity()
    }
}