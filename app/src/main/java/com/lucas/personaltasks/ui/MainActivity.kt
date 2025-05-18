package com.lucas.personaltasks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lucas.personaltasks.R
import com.lucas.personaltasks.adapter.TaskAdapter
import com.lucas.personaltasks.controller.MainController
import com.lucas.personaltasks.databinding.ActivityMainBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.Constant.EXTRA_TASK
import com.lucas.personaltasks.model.Constant.EXTRA_VIEW_TASK

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

    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(this, taskList)
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

                task?.let { receivedTask ->
                    val position = taskList.indexOfFirst { it.id == receivedTask.id }
                    if (position == -1) {
                        mainController.createTask(receivedTask)
                        taskList.add(receivedTask)
                    } else {
                        mainController.updateTask(receivedTask)
                        taskList[position] = receivedTask
                    }
                    taskAdapter.notifyDataSetChanged()
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura e preenche Lista de task
        amb.taskLv.adapter = taskAdapter
        fillTaskList()

        registerForContextMenu(amb.taskLv)
        amb.taskLv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Intent(this@MainActivity, TaskActivity::class.java).apply {
                    putExtra(EXTRA_TASK, taskList[position])
                    putExtra(EXTRA_VIEW_TASK, true)
                    startActivity(this)
                }
            }
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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        return when(item.itemId) {
            R.id.remove_task_mi -> {
                val taskToRemove = taskList[position]
                mainController.removeTask(taskToRemove)
                taskList.removeAt(position)
                taskAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Task removed", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.edit_task_mi -> {
                Intent(this, TaskActivity::class.java).apply {
                    putExtra(EXTRA_TASK, taskList[position])
                    carl.launch(this)
                }
                true
            } else -> { false }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterForContextMenu(amb.taskLv)
    }
}