package com.example.pomodorotechnique.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.pomodorotechnique.FragmentHistory
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
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
       /* // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1)*/
        return when(position){
            0 -> FragmentTimer()
            1 -> FragmentHistory()
            else -> FragmentTimer()
        }
    }

    


    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    //the amount of tabs. Here I'm creating one page fot the timer and one for the history
    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}