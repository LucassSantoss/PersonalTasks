package com.lucas.personaltasks.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.lucas.personaltasks.R
import com.lucas.personaltasks.controller.MainController
import com.lucas.personaltasks.databinding.ActivityMainBinding
import com.lucas.personaltasks.model.Task

class MainActivity : AppCompatActivity() {
    // Inicia ActivityMainBinding
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Inicia Controller
    private val mainController: MainController by lazy {
        MainController(this)
    }

    // Lista de tasks
    private val taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)


        // Configura e preenche Lista de task
        amb.taskLv.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            taskList
        )
        fillTaskList()
    }

    private fun createTask() {
        var task = Task(title = "task1", description = "description1")
        mainController.createTask(task)
    }

    private fun fillTaskList() {
        taskList.clear()
        Thread {
            taskList.addAll(mainController.getTasks())
        }.start()
    }
}