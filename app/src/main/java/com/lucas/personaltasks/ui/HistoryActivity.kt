package com.lucas.personaltasks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lucas.personaltasks.R
import com.lucas.personaltasks.adapter.HistoryTaskRvAdapter
import com.lucas.personaltasks.controller.HistoryController
import com.lucas.personaltasks.databinding.ActivityHistoryBinding
import com.lucas.personaltasks.model.Constant.EXTRA_TASK
import com.lucas.personaltasks.model.Constant.EXTRA_TASK_ARRAY
import com.lucas.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.TaskStatus
import com.lucas.personaltasks.ui.MainActivity.Companion

class HistoryActivity : AppCompatActivity(), OnHistoryTaskClickener {
    private val ahb: ActivityHistoryBinding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }

    private val taskList: MutableList<Task> = mutableListOf()

    private val historyController: HistoryController by lazy {
        HistoryController(this)
    }

    private val taskAdapter: HistoryTaskRvAdapter by lazy {
        HistoryTaskRvAdapter(this, taskList)
    }

    companion object {
        const val GET_TASKS_MESSAGE = 1
        const val GET_TASKS_INTERVAL = 2000L
    }

    val getTasksHandler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MainActivity.GET_TASKS_MESSAGE) {
                historyController.getHistoryTasks()
                sendMessageDelayed(
                    obtainMessage().apply {
                        what = MainActivity.GET_TASKS_MESSAGE
                    }, MainActivity.GET_TASKS_INTERVAL
                )
            } else {
                val taskArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    msg.data?.getParcelableArray(EXTRA_TASK_ARRAY, Task::class.java)
                } else {
                    msg.data?.getParcelableArray(EXTRA_TASK_ARRAY)
                }
                taskList.clear()
                taskArray?.forEach {
                    taskList.addAll(listOf(it as Task))
                }
                taskAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ahb.root)
        setSupportActionBar(ahb.toolbarIn.toolbar)
        supportActionBar?.title = "History"

        ahb.taskRv.adapter = taskAdapter
        ahb.taskRv.layoutManager = LinearLayoutManager(this)

        getTasksHandler.sendMessageDelayed(
            Message().apply {
                what = GET_TASKS_MESSAGE
            }, GET_TASKS_INTERVAL
        )
    }

    override fun onReactivateTaskMenuItemClick(position: Int) {
        val task = taskList[position]
        taskList.removeAt(position)
        historyController.recoveryTask(task)
        taskAdapter.notifyItemRemoved(position)
        Toast.makeText(this@HistoryActivity, "Task reactivated", Toast.LENGTH_SHORT).show()
    }

    override fun onDetailTaskMenuItemClick(position: Int) {
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_VIEW_TASK, true)
            startActivity(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_mi -> {
                finish()
                true
            }
            else -> { false }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser == null) finish()
    }
}