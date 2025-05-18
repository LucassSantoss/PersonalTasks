package com.lucas.personaltasks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lucas.personaltasks.databinding.ActivityTaskBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.Constant.EXTRA_TASK
import com.lucas.personaltasks.model.Constant.EXTRA_VIEW_TASK
import java.util.Calendar
import java.util.Date


class TaskActivity: AppCompatActivity() {
    private val acb: ActivityTaskBinding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }

    private var selectedDateTimeMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(acb.root)

        setSupportActionBar(acb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "New task"

        updateDateTimeText()

        val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra<Task>(EXTRA_TASK)
        }

        receivedTask?.let {
            supportActionBar?.subtitle = "Edit contact"
            with(acb) {
                titleEd.setText(it.title)
                descriptionEd.setText(it.description)
                val limitDate = Date(it.limitDateMillis)
                datepicker.updateDate(limitDate.year, limitDate.month, limitDate.day)

                val viewTask = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)
                if (viewTask) {
                    supportActionBar?.subtitle = "View task"
                    titleEd.isEnabled = false
                    descriptionEd.isEnabled = false
                    datepicker.isEnabled = false
                    saveBtn.visibility = View.GONE
                }
            }
        }

        with(acb) {
            saveBtn.setOnClickListener {
                val year: Int = datepicker.year
                val month: Int = datepicker.month
                val day: Int = datepicker.dayOfMonth

                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                val dateMillis = calendar.timeInMillis

                Task (
                    receivedTask?.id?:hashCode(),
                    title = titleEd.text.toString(),
                    description = descriptionEd.text.toString(),
                    limitDateMillis = dateMillis
                ).let { task ->
                    Intent().apply {
                        putExtra(EXTRA_TASK, task)
                        setResult(RESULT_OK, this)
                    }
                }
                finish()
            }

            cancelButton.setOnClickListener {
                Intent().apply {
                    setResult(RESULT_CANCELED)
                }
                finish()
            }

        }
    }

    private fun updateDateTimeText() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        acb.datepicker.updateDate(year, month, day)
    }
}