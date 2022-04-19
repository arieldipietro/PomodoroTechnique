package com.example.pomodorotechnique

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.pomodorotechnique.databinding.FragmentHistoryBinding
import com.example.pomodorotechnique.databinding.IndividualTaskViewBinding

class FragmentHistory : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var taskListContainer: ViewGroup
    private val tasksViewModel = TasksViewModel()
    //private val tasksViewModel = TasksViewModel() by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("MainActivity", "OnAttach Called")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "OnCreate Called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MainActivity", "onViewCreated Called")
    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "onStart Called")
    }

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

        //Checks if the list has some items, otherwise displays a message
        checkTasksList()

        tasksViewModel.tasksListData.observe(viewLifecycleOwner,{
            for (item in tasksViewModel.tasksList) {

                Log.i("MainActivity", "Observer called")

                val view: IndividualTaskViewBinding = DataBindingUtil.inflate(
                    inflater, R.layout.individual_task_view, container, false
                )

                view.taskTitle.text = item.name
                view.taskDateCreated.text = item.dateCreated
                view.taskTertiaryText.text = item.cyclesCompleted.toString()

                taskListContainer.addView(view.root)
            }

            checkTasksList()
            Log.i("MainActivity", "OncreateView Called")
        })

        return binding.root
    }

    private fun checkTasksList(){
        if(taskListContainer.childCount == 0 ){
            binding.emptyListText.setVisibility(View.VISIBLE)
        } else{
            binding.emptyListText.setVisibility(View.GONE)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity", "onPause Called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("MainActivity", "On resume called")
    }
}

