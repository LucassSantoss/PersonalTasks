package com.lucas.personaltasks.controller

import android.os.Message
import com.lucas.personaltasks.model.Constant.EXTRA_TASK_ARRAY
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.TaskDao
import com.lucas.personaltasks.model.TaskFirebaseDatabase
import com.lucas.personaltasks.model.TaskStatus
import com.lucas.personaltasks.ui.HistoryActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryController(private val historyActivity: HistoryActivity){
    // Instancia o taskDao utilizando o firebase database
    private val taskDao: TaskDao = TaskFirebaseDatabase()
    private val databaseCoroutineScope = CoroutineScope(Dispatchers.IO)

    // Chama funções do Dao utulizando coroutine

    fun getHistoryTasks() {
        databaseCoroutineScope.launch {
            val taskList = taskDao.getTasks()
            // Filtra apenas as tasks que já foram deletadas
            val history = taskList.filter { it.status == TaskStatus.DELETED }
            // Envia mensagem para o historyActivity com o resultado das tasks
            historyActivity.getTasksHandler.sendMessage(Message().apply {
                data.putParcelableArray(EXTRA_TASK_ARRAY, history.toTypedArray())
            })
        }
    }

    fun getTask(id: Int) = taskDao.getTask(id)

    fun recoveryTask(task: Task) {
        databaseCoroutineScope.launch {
            task.status = TaskStatus.OPEN
            taskDao.updateTask(task)
        }

    }
}