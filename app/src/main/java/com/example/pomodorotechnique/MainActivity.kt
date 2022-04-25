package com.example.pomodorotechnique

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.pomodorotechnique.ui.main.SectionsPagerAdapter
import com.example.pomodorotechnique.databinding.ActivityMainBinding
import com.example.pomodorotechnique.models.TimerState
import com.example.pomodorotechnique.utils.cancelNotifications
import com.example.pomodorotechnique.utils.sendNotification
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    /*private val timerViewModel by viewModels<TimerViewModel>()
    //private val tasksViewModel = ViewModelProvider(this).get(TasksViewModel::class.java)

    var timerState = timerViewModel.timerState.value.toString()
    var cyclesCount = timerViewModel.secondsRemaining.value!!
    var secondsRemaining = timerViewModel.cyclesCount*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            with(savedInstanceState) {
                // Restore value of members from saved state
                timerState = getString(TIMER_STATE)!!
                cyclesCount = getLong(CYCLES_COUNT)
                secondsRemaining = getInt(SECONDS_REMAINING)
            }
        } else {
            // Probably initialize members with default values for a new instance
        }*/

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()

    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        // Save the user's current game state
        outState?.run {
            putString(TIMER_STATE, timerViewModel.timerState.value.toString())
            putLong(SECONDS_REMAINING, timerViewModel.secondsRemaining.value!!)
            putInt(CYCLES_COUNT, timerViewModel.cyclesCount)
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    companion object {
        val TIMER_STATE = "timerState"
        val SECONDS_REMAINING = "secondsRemaining"
        val CYCLES_COUNT = "CyclesCount"
    }*/


}