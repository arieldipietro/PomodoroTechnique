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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabLayout: TabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if(position == 0){
                tab.text = "Timer"
            }
            else{
                tab.text = "History"
            }
        }.attach()

    }

    override fun onPause() {
        super.onPause()
    }
}