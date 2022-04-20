package com.example.pomodorotechnique

import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.databinding.FragmentHistoryBinding
import com.example.pomodorotechnique.databinding.IndividualTaskViewBinding

class FragmentHistory : Fragment() {

    private lateinit var binding : FragmentHistoryBinding
    private lateinit var tasksListContainer : ViewGroup
    private lateinit var tasksViewModel : TasksViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_history,
            container,
            false
        )

        tasksListContainer = binding.tasksListContainer

        tasksViewModel = ViewModelProvider(requireActivity()).get(TasksViewModel::class.java)

        //Checks if the list has some items, otherwise displays a message
        checkTasksList()

        tasksViewModel.tasksListData.observe(viewLifecycleOwner,{
            updateUI()
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    //overflow menu
    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item!!, requireView().findNavController())
        super.onOptionsItemSelected(item)
    }*/

    private fun checkTasksList(){
        if(tasksListContainer.childCount == 0 ){
            binding.emptyListText.setVisibility(View.VISIBLE)
        } else{
            binding.emptyListText.setVisibility(View.GONE)
        }

    }

    fun updateUI(){
        val inflater = LayoutInflater.from(context)

        tasksListContainer.removeAllViews()

        for (item in tasksViewModel.tasksList) {
            val view: IndividualTaskViewBinding = DataBindingUtil.inflate(
                inflater, R.layout.individual_task_view, tasksListContainer, false
            )

            view.taskTitle.text = item.name
            view.taskDateCreated.text = getString(R.string.task_date_created)+" "+item.dateCreated
            view.taskCyclesCompleted.text = getString(R.string.task_cycles_completed) +" "+item.cyclesCompleted.toString()

            tasksListContainer.addView(view.root)
        }

        checkTasksList()
    }





}