package com.lucas.personaltasks.controller

import androidx.room.Room
import com.lucas.personaltasks.ui.MainActivity
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.TaskDao
import com.lucas.personaltasks.model.TaskRoomDb

class MainController(mainActivity: MainActivity) {
    private val taskDao: TaskDao = Room.databaseBuilder(
        mainActivity, TaskRoomDb::class.java, "task-database"
    ).build().taskDao()

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