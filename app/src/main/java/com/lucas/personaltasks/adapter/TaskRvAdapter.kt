package com.lucas.personaltasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.lucas.personaltasks.R
import com.lucas.personaltasks.databinding.TileTaskBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.ui.OnTaskClickListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskRvAdapter(
    private val onTaskClickListener: OnTaskClickListener,
    private val taskList: MutableList<Task>
): RecyclerView.Adapter<TaskRvAdapter.TaskViewHolder>() {

    // ViewHolder que mantém referências das views de cada elemento
    inner class TaskViewHolder(ttb: TileTaskBinding): RecyclerView.ViewHolder(ttb.root) {
        val titleEd: TextView = ttb.titleTv
        val descriptionEd: TextView = ttb.descriptionTv
        val date: TextView = ttb.datetimeTv
        val finished: TextView = ttb.finishedTv

        init {
            // Configura menu de contexto
            ttb.root.setOnCreateContextMenuListener { menu, v, menuInfo ->
                // Infla o menu de contexto
                (onTaskClickListener as AppCompatActivity).menuInflater.inflate(
                    R.menu.context_menu_main, menu
                )

                // Ação realizada ao clicar em editar task
                menu.findItem(R.id.edit_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onEditTaskMenuItemClick(adapterPosition)
                    true
                }

                // Ação realizada ao clicar em remover task
                menu.findItem(R.id.remove_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onRemoveTaskMenuItemClick(adapterPosition)
                    true
                }

                // Ação realizada ao clicar em detalhar task
                menu.findItem(R.id.detail_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onDetailTaskMenuItemClick(adapterPosition)
                    true
                }
            }

            // Um clique chama a função onTaskClick
            ttb.root.setOnClickListener {
                onTaskClickListener.onTaskClick(adapterPosition)
            }
        }
    }

    // Cria um novo ViewHolder inflando o layout do item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder = TaskViewHolder(
        TileTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    // Faz o bind dos dados da posição "position" nas views do ViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        taskList[position].let { task ->
            with(holder) {
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                if (task.title.isEmpty()) {
                    titleEd.text = "Empty"
                } else {
                    titleEd.text = task.title
                }
                titleEd.text = titleEd.text.toString() + " - " + task.priority.toString()
                var description: String
                if (task.description.isEmpty()) {
                    description = "Empty"
                } else {
                    description = task.description
                }
                descriptionEd.text = description
                date.text = fmt.format(Date(task.limitDateMillis))
                finished.text = "Finished: " + task.finished.toString()
            }
        }
    }

    // Retorna a quantidade de itens da lista
    override fun getItemCount(): Int = taskList.size
}