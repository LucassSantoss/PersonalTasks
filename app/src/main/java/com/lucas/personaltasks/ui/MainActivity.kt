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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lucas.personaltasks.R
import com.lucas.personaltasks.adapter.TaskRvAdapter
import com.lucas.personaltasks.controller.MainController
import com.lucas.personaltasks.databinding.ActivityMainBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.Constant.EXTRA_TASK
import com.lucas.personaltasks.model.Constant.EXTRA_TASK_ARRAY
import com.lucas.personaltasks.model.Constant.EXTRA_VIEW_TASK

class MainActivity : AppCompatActivity(), OnTaskClickListener{
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainController: MainController by lazy {
        MainController(this)
    }

    private val taskList: MutableList<Task> = mutableListOf()

    private lateinit var carl: ActivityResultLauncher<Intent>

    private val taskAdapter: TaskRvAdapter by lazy {
        TaskRvAdapter(this, taskList)
    }

    companion object {
        const val GET_TASKS_MESSAGE = 1
        const val GET_TASKS_INTERVAL = 2000L
    }

    val getTasksHandler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == GET_TASKS_MESSAGE) {
                mainController.getTasks()
                sendMessageDelayed(
                    obtainMessage().apply {
                        what = GET_TASKS_MESSAGE
                    }, GET_TASKS_INTERVAL
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
        setContentView(amb.root)

        // Vincula com a toolbar criada
        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "Task List"

        // Bloco que registra o que deve acontecer após uma ação/Intent chamada pelo 'carl'
        carl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == RESULT_OK) {
                // Recebe a Task passada pela Intent
                val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(EXTRA_TASK, Task::class.java)
                } else {
                    result.data?.getParcelableExtra<Task>(EXTRA_TASK)
                }

                task?.let { receivedTask ->
                    // Procura a task na lista, se não encontrar, cria a task
                    // Caso já exista na lista, vai editar a task
                    val position = taskList.indexOfFirst { it.id == receivedTask.id }
                    if (position == -1) {
                        mainController.createTask(receivedTask)
                        taskList.add(receivedTask)
                        taskAdapter.notifyItemInserted(taskList.lastIndex)
                    } else {
                        mainController.updateTask(receivedTask)
                        taskList[position] = receivedTask
                        taskAdapter.notifyItemChanged(position)
                    }
                }
            // Caso o usuário clique no botão de cancelar
            } else if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show()
            }
        }

        // Configura lista de Tasks (RecycleView)
        amb.taskRv.adapter = taskAdapter
        amb.taskRv.layoutManager = LinearLayoutManager(this)

        getTasksHandler.sendMessageDelayed(
            Message().apply {
                what = GET_TASKS_MESSAGE
            }, GET_TASKS_INTERVAL
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_task_mi -> {
                // Abre a tela TaskActivity para criar uma nova Task
                // Quando clicamos no botão de adicionar uma task
                carl.launch(Intent(this, TaskActivity::class.java))
                true
            }
            R.id.sign_out_mi -> {
                Firebase.auth.signOut()
                finish()
                true
            }
            else -> { false }
        }
    }

    override fun onTaskClick(position: Int) {
        // Abre a tela TaskActivity apenas para visualização
        // Passa a task para visualizar, e um parâmetro que diz que é para visualização
        // Não passa o 'carl' pois não precisa de nenhuma execução após a intent, e não devolve nada
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_VIEW_TASK, true)
            startActivity(this)
        }
    }

    override fun onRemoveTaskMenuItemClick(position: Int) {
        // Remove uma task
        val task = taskList[position]
        taskList.removeAt(position)
        mainController.removeTask(task)
        taskAdapter.notifyItemRemoved(position)
        Toast.makeText(this, "Task Removed!", Toast.LENGTH_SHORT).show()
    }

    override fun onEditTaskMenuItemClick(position: Int) {
        // Abre a tela TaskActivity para edição, passando a task para editar
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            carl.launch(this)
        }
    }

    override fun onDetailTaskMenuItemClick(position: Int) {
        // Abre a tela TaskActivity apenas para visualização
        // Passa a task para visualizar, e um parâmetro que diz que é para visualização
        // Não passa o 'carl' pois não precisa de nenhuma execução após a intent, e não devolve nada
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_VIEW_TASK, true)
            startActivity(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser == null) finish()
    }
}