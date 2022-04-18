package com.example.pomodorotechnique

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.databinding.FragmentHistoryBinding
import com.example.pomodorotechnique.databinding.IndividualTaskViewBinding

class FragmentHistory : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var taskListContainer: ViewGroup
    private lateinit var tasksViewModel: TasksViewModel

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

        taskListContainer = binding.tasksListContainer

        tasksViewModel = ViewModelProvider(requireActivity()).get(TasksViewModel::class.java)


        //Checks if the list has some items, otherwise displays a message
        checkTasksList()

        tasksViewModel.tasksListData.observe(viewLifecycleOwner,{
            for (item in it) {

                val view: IndividualTaskViewBinding = DataBindingUtil.inflate(
                    inflater, R.layout.individual_task_view, container, false
                )

                view.taskTitle.text = item.name
                view.taskDateCreated.text = item.dateCreated
                view.taskTertiaryText.text = item.cyclesCompleted.toString()

                taskListContainer.addView(view.root)
            }

            checkTasksList()
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun checkTasksList(){
        if(taskListContainer.childCount == 0 ){
            binding.emptyListText.setVisibility(View.VISIBLE)
        } else{
            binding.emptyListText.setVisibility(View.GONE)
        }
    }

}

