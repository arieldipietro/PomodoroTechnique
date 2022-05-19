package com.example.pomodorotechnique.screens

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pomodorotechnique.R
import com.example.pomodorotechnique.databinding.FragmentTabsContainerBinding
import com.example.pomodorotechnique.ui.main.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class FragmentTabsContainer : Fragment() {

    private lateinit var binding : FragmentTabsContainerBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tabs_container,
            container,
            false
        )

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.viewPager2

        //creating the adapter
        //if we want to create the adapter in a fragment, we can use
        //val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            if(position == 0){
                tab.text = "Timer"
            }
            else{
                tab.text = "History"
            }
        }.attach()


        setHasOptionsMenu(true)

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
/*        R.id.navigation_settings -> {
            findNavController().navigate(R.id.action_fragmentTabsContainer_to_navigation_settings)
            true
        }*/
        R.id.navigation_about -> {
            findNavController().navigate(R.id.action_fragmentTabsContainer_to_navigation_about)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}





}
