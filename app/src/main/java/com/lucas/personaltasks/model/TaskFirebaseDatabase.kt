package com.lucas.personaltasks.model

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TaskFirebaseDatabase: TaskDao {
    private val databaseReference = Firebase.database.getReference("taskList")
    private val taskList = mutableListOf<Task>()

    init {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val task = snapshot.getValue<Task>()
                task?.let { newTask ->
                    if (!taskList.any { it.id == newTask.id }) {
                        taskList.add(newTask)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val task = snapshot.getValue<Task>()
                task?.let { editedTask ->
                    taskList[taskList.indexOfFirst { it.id == editedTask.id }] = editedTask

                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val task = snapshot.getValue<Task>()
                task?.let {
                    taskList.remove(it)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // NSA
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }

        })

        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskMap = snapshot.getValue<Map<String, Task>>()
                taskList.clear()
                taskMap?.values?.also { taskList.addAll(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }

        })
    }


    override fun createTask(task: Task): Long {
        databaseReference.child(task.id.toString()).setValue(task)
        return 1L
    }

    override fun getTask(id: Int) = taskList[taskList.indexOfFirst { it.id == id }]

    override fun getTasks(): MutableList<Task> {
        return taskList
    }

    override fun updateTask(task: Task): Int {
        databaseReference.child(task.id.toString()).setValue(task)
        return 1
    }

    override fun deleteTask(task: Task): Int {
        databaseReference.child(task.id.toString()).removeValue()
        return 1
    }

}