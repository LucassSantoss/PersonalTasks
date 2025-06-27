package com.lucas.personaltasks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.lucas.personaltasks.R
import com.lucas.personaltasks.databinding.TileTaskBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.ui.OnHistoryTaskClickener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryTaskRvAdapter(
    private val onHistoryTaskClickener: OnHistoryTaskClickener,
    private val historyList: MutableList<Task>
): RecyclerView.Adapter<HistoryTaskRvAdapter.TaskViewHolder>() {

    // ViewHolder que mantém referências das views de cada elemento
    inner class TaskViewHolder(ttb: TileTaskBinding): RecyclerView.ViewHolder(ttb.root) {
        val titleEd: TextView = ttb.titleTv
        val descriptionEd: TextView = ttb.descriptionTv
        val date: TextView = ttb.datetimeTv
        val finished: TextView = ttb.finishedTv

        init {
            ttb.root.setOnCreateContextMenuListener { menu, v, menuInfo ->
                // Configura o menu de context das tasks
                (onHistoryTaskClickener as AppCompatActivity).menuInflater.inflate(
                    R.menu.context_menu_history, menu
                )

                // Ação realizada ao clicar em reativar uma task
                menu.findItem(R.id.reactivate_task_mi).setOnMenuItemClickListener {
                    onHistoryTaskClickener.onReactivateTaskMenuItemClick(adapterPosition)
                    true
                }

                // Ação realizada ao clicar em detalhar uma task
                menu.findItem(R.id.detail_task_mi).setOnMenuItemClickListener {
                    onHistoryTaskClickener.onDetailTaskMenuItemClick(adapterPosition)
                    true
                }
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
        historyList[position].let { task ->
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
    override fun getItemCount(): Int = historyList.size
}