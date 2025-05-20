package com.lucas.personaltasks.controller

import androidx.room.Room
import com.lucas.personaltasks.ui.MainActivity
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.TaskDao
import com.lucas.personaltasks.model.TaskRoomDb

class MainController(mainActivity: MainActivity) {
    // Instancia uma classe que implementa o Dao utilizando o RoomDb
    private val taskDao: TaskDao = Room.databaseBuilder(
        mainActivity, TaskRoomDb::class.java, "task-database"
    ).build().taskDao()

    // Implementa funções que chamam o Dao utilizando Threads
    fun createTask(task: Task) {
        Thread {
            taskDao.createTask(task)
        }.start()
    }

    fun getTask(id: Int) {
        Thread {
            taskDao.getTask(id)
        }.start()
    }

    // Não usa thread pois já fiz na MainActivity, atualizando a lista em um "runOnUiThread"
    fun getTasks() = taskDao.getTasks()

    fun updateTask(task: Task) {
        Thread {
            taskDao.updateTask(task)
        }.start()
    }

    fun removeTask(task: Task) {
        Thread {
            taskDao.deleteTask(task)
        }.start()
    }
}