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

    // Adaptador do RecyclerView utilizado na tela de histórico
    private val taskAdapter: HistoryTaskRvAdapter by lazy {
        HistoryTaskRvAdapter(this, taskList)
    }

    companion object {
        // Variáveis estáticas usadas para controlar a atualização da lista de tasks
        const val GET_TASKS_MESSAGE = 1
        const val GET_TASKS_INTERVAL = 2000L
    }

    // instância de uma classe anônima que estende a classe Handler
    val getTasksHandler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // Executado se a mensagem tiver o valor de GET_TASKS_MESSAGE
            if (msg.what == GET_TASKS_MESSAGE) {
                historyController.getHistoryTasks()
                // Reenvia mensagem com o mesmo código, para que continue atualizando as tasks
                sendMessageDelayed(
                    obtainMessage().apply {
                        what = GET_TASKS_MESSAGE
                    }, GET_TASKS_INTERVAL
                )
            } else {
                // Trata o caso em que as tasks são recebidas após a chamada do historyController
                // Limpa a lista atual e adiciona as tasks novamente
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

        // Configura toolbar
        setSupportActionBar(ahb.toolbarIn.toolbar)
        supportActionBar?.title = "History"

        // Configura RecyclerView
        ahb.taskRv.adapter = taskAdapter
        ahb.taskRv.layoutManager = LinearLayoutManager(this)

        // Envia primeira mensagem para atualizar as tasks
        getTasksHandler.sendMessageDelayed(
            Message().apply {
                what = GET_TASKS_MESSAGE
            }, GET_TASKS_INTERVAL
        )
    }

    // Função ativada ao clicar em reativar uma task
    // Remove da lista de histórico e troca o status por meio do historyController
    // A task voltará para a página inicial
    override fun onReactivateTaskMenuItemClick(position: Int) {
        val task = taskList[position]
        taskList.removeAt(position)
        historyController.recoveryTask(task)
        taskAdapter.notifyItemRemoved(position)
        Toast.makeText(this@HistoryActivity, "Task reactivated", Toast.LENGTH_SHORT).show()
    }

    // Função ativada ao clicar em detalhar uma task
    // Abre uma tela TaskActivity com a task selecionada para visualização
    override fun onDetailTaskMenuItemClick(position: Int) {
        Intent(this, TaskActivity::class.java).apply {
            putExtra(EXTRA_TASK, taskList[position])
            putExtra(EXTRA_VIEW_TASK, true)
            startActivity(this)
        }
    }

    // Infla o menu de opções
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Configura botão de voltar para fechar essa tela de histórico
            R.id.home_mi -> {
                finish()
                true
            }
            else -> { false }
        }
    }

    override fun onStart() {
        super.onStart()
        // Verifica se o usuário está logado
        if (Firebase.auth.currentUser == null) finish()
    }
}