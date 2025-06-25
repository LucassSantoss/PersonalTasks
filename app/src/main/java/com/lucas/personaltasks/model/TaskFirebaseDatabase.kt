package com.lucas.personaltasks.model

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// Implementa funções do DAO utilizando o firebase
class TaskFirebaseDatabase: TaskDao {
    // Faz referência ao caminho/nó taskList salvo no banco de dados do firebase
    private val databaseReference = Firebase.database.getReference("taskList")
    // Lista que espelha o banco de dados
    private val taskList = mutableListOf<Task>()

    init {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val task = snapshot.getValue<Task>()
                // Garante que não é nulo ou com mesmo id de outra task antes de adicionar
                task?.let { newTask ->
                    if (!taskList.any { it.id == newTask.id }) {
                        taskList.add(newTask)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val task = snapshot.getValue<Task>()
                // Atualiza a task na taskList
                task?.let { editedTask ->
                    taskList[taskList.indexOfFirst { it.id == editedTask.id }] = editedTask

                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val task = snapshot.getValue<Task>()
                // Remove a task da taskList
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
                // Atualiza toda a lista
                taskList.clear()
                taskMap?.values?.also { taskList.addAll(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }

        })
    }


    override fun createTask(task: Task): Long {
        // Cria uma nova task no banco
        databaseReference.child(task.id.toString()).setValue(task)
        return 1L
    }

    // Retorna uma task pelo id
    override fun getTask(id: Int) = taskList[taskList.indexOfFirst { it.id == id }]

    // Retorna todas as tasks
    override fun getTasks(): MutableList<Task> {
        return taskList
    }

    // Atualiza uma task no banco
    override fun updateTask(task: Task): Int {
        databaseReference.child(task.id.toString()).setValue(task)
        return 1
    }

    // Deleta uma task do banco
    override fun deleteTask(task: Task): Int {
        databaseReference.child(task.id.toString()).removeValue()
        return 1
    }

}