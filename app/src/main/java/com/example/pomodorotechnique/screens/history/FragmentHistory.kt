package com.example.pomodorotechnique.screens.history

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pomodorotechnique.R
import com.example.pomodorotechnique.TimerViewModel
import com.example.pomodorotechnique.database.Task
import com.example.pomodorotechnique.database.TasksDatabase
import com.example.pomodorotechnique.database.TasksDatabaseDao
import com.example.pomodorotechnique.databinding.FragmentHistoryBinding
import com.example.pomodorotechnique.tasks.ViewModelFactory
import com.google.android.material.tabs.TabLayout

class FragmentHistory : Fragment(), ItemClickListener {


    private lateinit var binding: FragmentHistoryBinding
    private lateinit var tasksListContainer: ViewGroup

    private lateinit var currentTask: Task
    private lateinit var datasource: TasksDatabaseDao

    private lateinit var timerViewModel: TimerViewModel

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

        val application = requireNotNull(this.activity).application
        datasource = TasksDatabase.getInstance(application).tasksDatabaseDao

        val viewModelFactory = ViewModelFactory(datasource, application)

        timerViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[TimerViewModel::class.java]

        binding.timerViewModel = timerViewModel
        binding.lifecycleOwner = this

        //Instantiating the Adapter
        val adapter = TasksAdapter(this)
        binding.tasksList.adapter = adapter

        //binding elements in the database to the UI
        timerViewModel.allTasks.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

    override fun navigateClick(taskId: Long) {

        timerViewModel.cancelTimers()

        val tabLayout: TabLayout = this.requireActivity().findViewById(R.id.tabs)
        tabLayout.getTabAt(0)?.select()

        timerViewModel.setSelectedTaskId(taskId)
        timerViewModel.getSelectedTaskFromDatabase(taskId)
    }

    override fun deleteClick(taskId: Long) {
        timerViewModel.deleteTask(taskId)
    }
}
