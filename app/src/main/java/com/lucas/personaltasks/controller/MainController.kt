package com.lucas.personaltasks.controller

import android.os.Message
import androidx.room.Room
import com.lucas.personaltasks.model.Constant.EXTRA_TASK_ARRAY
import com.lucas.personaltasks.ui.MainActivity
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.TaskDao
import com.lucas.personaltasks.model.TaskFirebaseDatabase
import com.lucas.personaltasks.model.TaskRoomDb
import com.lucas.personaltasks.model.TaskStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainController(private val mainActivity: MainActivity) {
    /*
    // Instancia uma classe que implementa o Dao utilizando o RoomDb
    private val taskDao: TaskDao = Room.databaseBuilder(
        mainActivity, TaskRoomDb::class.java, "task-database"
    ).build().taskDao()
     */

    // Instancia o taskDao utilizando o FirebaseDatabase
    private val taskDao: TaskDao = TaskFirebaseDatabase()
    private val databaseCoroutineScope = CoroutineScope(Dispatchers.IO)

    // Chama funções do DAO utilizando coroutine

    fun createTask(task: Task) {
        databaseCoroutineScope.launch {
            taskDao.createTask(task)
        }
    }

    fun getTask(id: Int) = taskDao.getTask(id)

    fun getTasks() {
        databaseCoroutineScope.launch {
            val taskList = taskDao.getTasks()
            // Filtra apenas as tasks que não foram deletadas
            val tasks = taskList.filter { it.status == TaskStatus.OPEN }
            // Envia mensagem para a mainActivity com o resultado das tasks
            mainActivity.getTasksHandler.sendMessage(Message().apply {
                data.putParcelableArray(EXTRA_TASK_ARRAY, tasks.toTypedArray())
            })
        }
    }

    fun updateTask(task: Task) {
        databaseCoroutineScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun removeTask(task: Task) {
        databaseCoroutineScope.launch {
            task.status = TaskStatus.DELETED
            taskDao.updateTask(task)
        }
    }
}