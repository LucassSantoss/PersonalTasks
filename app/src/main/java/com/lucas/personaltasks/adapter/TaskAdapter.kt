package com.lucas.personaltasks.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.lucas.personaltasks.R
import com.lucas.personaltasks.databinding.TileTaskBinding
import com.lucas.personaltasks.model.Task
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    context: Context,
    private val taskList: MutableList<Task>
): ArrayAdapter<Task>(
    context,
    R.layout.tile_task,
    taskList
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val task = taskList[position]

        var taskTileView = convertView
        if (taskTileView == null) {
            TileTaskBinding.inflate(
                context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                parent,
                false
            ).apply {
                val tileTaskViewHolder = TileTaskViewHolder(titleTv, descriptionTv, datetimeTv)
                taskTileView = root
                (taskTileView as LinearLayout).tag = tileTaskViewHolder
            }
        }

        val viewHolder = taskTileView?.tag as TileTaskViewHolder
        viewHolder.titleTv.text = task.title
        val description: String = task.description.ifEmpty { "empty" }
        viewHolder.descriptionTv.text = description
        val fmt = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
        viewHolder.datetime_tv.text = fmt.format(Date(task.limitDateMillis))

        return taskTileView as View
    }

    private data class TileTaskViewHolder(
        val titleTv: TextView, val descriptionTv: TextView, val datetime_tv: TextView
    )
}