package com.lucas.personaltasks.model

import androidx.room.Database
import androidx.room.RoomDatabase

// Configura o RoomDb com as funções definidas em TaskDao
@Database(entities = [Task::class], version = 1)
abstract class TaskRoomDb: RoomDatabase() {
    abstract fun taskDao(): TaskDao
}