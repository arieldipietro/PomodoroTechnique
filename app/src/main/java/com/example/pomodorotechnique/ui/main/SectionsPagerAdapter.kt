package com.example.pomodorotechnique.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pomodorotechnique.screens.history.FragmentHistory
import com.example.pomodorotechnique.FragmentTimer
import com.example.pomodorotechnique.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
       /* // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1)*/
        return when(position){
            0 -> FragmentTimer()
            1 -> FragmentHistory()
            else -> FragmentTimer()
        }
    }


  /*  fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }*/

    //the amount of tabs. Here I'm creating one page fot the timer and one for the history
    override fun getItemCount(): Int {
        // Show 2 total pages.
        return 2
    }



}