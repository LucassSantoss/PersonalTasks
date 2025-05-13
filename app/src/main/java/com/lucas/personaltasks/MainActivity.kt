package com.lucas.personaltasks

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lucas.personaltasks.controller.MainController
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.TaskDao
import com.lucas.personaltasks.model.TaskRoomDb

class MainActivity : AppCompatActivity() {
    private val mainController: MainController by lazy {
        MainController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createTask()
    }

    fun createTask() {
        var task = Task(title = "task1", description = "description1")
        mainController.createTask(task)
    }
}