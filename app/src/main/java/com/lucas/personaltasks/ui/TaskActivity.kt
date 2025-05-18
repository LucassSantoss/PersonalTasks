package com.lucas.personaltasks.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.lucas.personaltasks.databinding.ActivityTaskBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.ui.Constant.EXTRA_TASK
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
        supportActionBar?.subtitle = "Task"

        updateDateTimeText()

        with(acb) {
            saveBtn.setOnClickListener {
                val year: Int = datepicker.getYear()
                val month: Int = datepicker.getMonth()
                val day: Int = datepicker.getDayOfMonth()

                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                val dateMillis = calendar.timeInMillis

                Task (
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