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
    inner class TaskViewHolder(ttb: TileTaskBinding): RecyclerView.ViewHolder(ttb.root) {
        val titleEd: TextView = ttb.titleTv
        val descriptionEd: TextView = ttb.descriptionTv
        val date: TextView = ttb.datetimeTv

        init {
            ttb.root.setOnCreateContextMenuListener { menu, v, menuInfo ->
                (onTaskClickListener as AppCompatActivity).menuInflater.inflate(
                    R.menu.context_menu_main, menu
                )

                menu.findItem(R.id.edit_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onEditTaskMenuItemClick(adapterPosition)
                    true
                }

                menu.findItem(R.id.remove_task_mi).setOnMenuItemClickListener {
                    onTaskClickListener.onRemoveTaskMenuItemClick(adapterPosition)
                    true
                }
            }

            ttb.root.setOnClickListener {
                onTaskClickListener.onTaskClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder = TaskViewHolder(
        TileTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        taskList[position].let { task ->
            with(holder) {
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                titleEd.text = task.title
                var description: String
                if (task.description.isEmpty()) {
                    description = "Empty"
                } else {
                    description = task.description
                }
                descriptionEd.text = description
                date.text = fmt.format(Date(task.limitDateMillis))
            }
        }
    }

    override fun getItemCount(): Int = taskList.size
}