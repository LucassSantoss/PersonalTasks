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
    fun getTask(id: Int) = taskDao.getTask(id)
    fun getTasks() = taskDao.getTasks()
    fun updateTask(task: Task) = taskDao.updateTask(task)
    fun removeTask(task: Task) = taskDao.deleteTask(task)
}