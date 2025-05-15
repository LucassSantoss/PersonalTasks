package com.lucas.personaltasks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lucas.personaltasks.R
import com.lucas.personaltasks.controller.MainController
import com.lucas.personaltasks.databinding.ActivityMainBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.ui.Constant.EXTRA_TASK

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

    private lateinit var carl: ActivityResultLauncher<Intent>

    private val taskAdapter: ArrayAdapter<Task> by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            taskList
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "Task List"

        carl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == RESULT_OK) {
                val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
                } else {
                    result.data?.getParcelableExtra<Task>(EXTRA_TASK)
                }
                task?.let {
                    mainController.createTask(it)
                    taskList.add(it)
                    taskAdapter.notifyDataSetChanged()
                }
            }
        }

        // Configura e preenche Lista de task
        amb.taskLv.adapter = taskAdapter
        fillTaskList()
    }

    private fun fillTaskList() {
        Thread {
            val tasks = mainController.getTasks()
            runOnUiThread {
                taskList.clear()
                taskList.addAll(tasks)
                taskAdapter.notifyDataSetChanged()
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_task_mi -> {
                carl.launch(Intent(this, TaskActivity::class.java))
                true
            } else -> { false }
        }
    }
}