package tech.edroomdevs.edroom.activity.splash_onboarding.carouselview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private var pages = mutableListOf<CarouselPage>()

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = CarouselPageFragment(pages[position])

    fun setPages(pages: List<CarouselPage>) {
        this.pages.addAll(pages)
    }
}