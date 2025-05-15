package com.lucas.personaltasks.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.lucas.personaltasks.R
import com.lucas.personaltasks.databinding.ActivityTaskBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.ui.Constant.EXTRA_TASK
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        acb.selectDatetimeBtn.setOnClickListener {
            showDateTimePicker()
        }

        updateDateTimeText()

        with(acb) {
            saveBtn.setOnClickListener {
                Task (
                    title = titleEd.text.toString(),
                    description = descriptionEd.text.toString(),
                    limitDateMillis = selectedDateTimeMillis

                ).let { task ->
                    Intent().apply {
                        putExtra(EXTRA_TASK, task)
                        setResult(RESULT_OK, this)
                    }
                }
                finish()
            }
        }
    }

    private fun showDateTimePicker() {
        // usa a data/hora já escolhida como default no picker
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateTimeMillis }

        // DatePicker
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // atualiza ano/mês/dia
                cal.set(year, month, dayOfMonth)

                // TimePicker após escolher data
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        // atualiza horas e minutos
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        cal.set(Calendar.MINUTE, minute)
                        selectedDateTimeMillis = cal.timeInMillis
                        updateDateTimeText()
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateTimeText() {
        val fmt = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
        acb.datetimeTv.text = fmt.format(Date(selectedDateTimeMillis))
    }
}