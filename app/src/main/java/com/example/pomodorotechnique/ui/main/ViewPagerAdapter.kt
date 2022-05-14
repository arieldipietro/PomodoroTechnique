package com.example.pomodorotechnique.ui.main


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pomodorotechnique.screens.FragmentTimer
import com.example.pomodorotechnique.screens.history.FragmentHistory

class ViewPagerAdapter(fragmentManager: FragmentManager, lifeycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifeycle) {
    override fun getItemCount(): Int {
        //return the total count of our fragments
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        //returns the actual fragments
        return when (position) {
            0 -> {
                FragmentTimer()
            }
            1 -> {
                FragmentHistory()
            }
            else -> {
                FragmentTimer()
            }

        }

    }


}