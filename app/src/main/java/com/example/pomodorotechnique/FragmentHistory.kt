package com.example.pomodorotechnique

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.databinding.FragmentHistoryBinding
import com.example.pomodorotechnique.databinding.IndividualTaskViewBinding
import com.example.pomodorotechnique.models.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class FragmentHistory : Fragment() {

    private lateinit var binding : FragmentHistoryBinding
    private lateinit var tasksListContainer : ViewGroup
    private lateinit var tasksViewModel : TasksViewModel
    private lateinit var currentTask : Task

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


        if(tasksViewModel.currentTasksList.isNotEmpty()) {
            currentTask = tasksViewModel.currentTasksList[0]
        }


        //Checks if the list has some items, otherwise displays a message
        checkCurrentTasksList()

        tasksViewModel.tasksListData.observe(viewLifecycleOwner,{
                updateHistoryUI()
        })

        setHasOptionsMenu(true)


        return binding.root
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity","Fragment history on resume called")
        Log.i("MainActivity","currentTaskList: ${tasksViewModel.currentTasksList}")
        if(tasksViewModel.currentTasksList.isNotEmpty()) {
            currentTaskNotEmpty()
            currentTask = tasksViewModel.currentTasksList[0]
            updateCurrentTaskUI()
        }

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

    private fun checkCurrentTasksList(){
        if(tasksViewModel.currentTasksList.isEmpty()){
            currentTaskEmpty()
        } else{
            currentTaskNotEmpty()
        }
    }

    private fun currentTaskEmpty(){
        binding.emptyListText.setVisibility(View.VISIBLE)
        binding.currentTaskText.setVisibility(View.GONE)
        binding.historyTasksText.setVisibility(View.GONE)
        binding.currentTaskContainer.setVisibility(View.GONE)
    }

    private fun currentTaskNotEmpty(){
        binding.emptyListText.setVisibility(View.GONE)
        binding.currentTaskText.setVisibility(View.VISIBLE)
        binding.historyTasksText.setVisibility(View.VISIBLE)
        binding.currentTaskContainer.setVisibility(View.VISIBLE)
        currentTask = tasksViewModel.currentTasksList[0]
    }

    fun updateHistoryUI(){
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
    }

    fun updateCurrentTaskUI(){
        binding.currentTaskTitle.text = currentTask.name
        binding.currentTaskDateCreated.text = getString(R.string.task_date_created)+" "+currentTask.dateCreated.toString()
        binding.currentTaskCyclesCompleted.text = getString(R.string.task_cycles_completed) +" "+currentTask.cyclesCompleted.toString()
    }


    


}