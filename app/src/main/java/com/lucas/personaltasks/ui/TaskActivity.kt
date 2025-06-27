package com.lucas.personaltasks.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import com.lucas.personaltasks.databinding.ActivityTaskBinding
import com.lucas.personaltasks.model.Task
import com.lucas.personaltasks.model.Constant.EXTRA_TASK
import com.lucas.personaltasks.model.Constant.EXTRA_VIEW_TASK
import com.lucas.personaltasks.model.Priority
import java.util.Calendar
import java.util.Date


class TaskActivity: AppCompatActivity() {
    private val acb: ActivityTaskBinding by lazy {
        ActivityTaskBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(acb.root)

        // Vincula com a toolbar criada
        setSupportActionBar(acb.toolbarIn.toolbar)
        supportActionBar?.subtitle = "New task"

        // Atualiza o DatePicker com a data atual
        updateDateTimeText()

        // Recebe a task passada pela Intent da MainActivity
        val receivedTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_TASK, Task::class.java)
        } else {
            intent.getParcelableExtra<Task>(EXTRA_TASK)
        }

        receivedTask?.let {
            supportActionBar?.subtitle = "Edit contact"
            // Preenche elementos visuais para edição e/ou visualização
            with(acb) {
                titleEd.setText(it.title)
                descriptionEd.setText(it.description)
                finishedCb.isChecked = it.finished
                checkActualPriority(acb, it)

                val cal = Calendar.getInstance().apply {
                    timeInMillis = it.limitDateMillis
                }
                datepicker.updateDate(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )

                val viewTask = intent.getBooleanExtra(EXTRA_VIEW_TASK, false)
                // Se for apenas visualização, desativa os elementos da tela
                if (viewTask) {
                    supportActionBar?.subtitle = "View task"
                    titleEd.isEnabled = false
                    descriptionEd.isEnabled = false
                    datepicker.isEnabled = false
                    finishedCb.isEnabled = false
                    saveBtn.visibility = View.GONE
                    lowPriorityCb.isEnabled = false
                    midPriorityCb.isEnabled = false
                    highPriorityCb.isEnabled = false
                }
            }
        }

        with(acb) {
            // Configura botão de salvar, cancelar e prioridades

            lowPriorityCb.setOnClickListener {
                midPriorityCb.isChecked = false
                highPriorityCb.isChecked = false
            }

            midPriorityCb.setOnClickListener {
                lowPriorityCb.isChecked = false
                highPriorityCb.isChecked = false
            }

            highPriorityCb.setOnClickListener {
                lowPriorityCb.isChecked = false
                midPriorityCb.isChecked = false
            }

            saveBtn.setOnClickListener {
                val year: Int = datepicker.year
                val month: Int = datepicker.month
                val day: Int = datepicker.dayOfMonth

                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                val dateMillis = calendar.timeInMillis

                // Cria uma nova task ao clicar em salvar, e envia pela Intent
                Task (
                    receivedTask?.id?:hashCode(),
                    title = titleEd.text.toString(),
                    description = descriptionEd.text.toString(),
                    limitDateMillis = dateMillis,
                    finished = finishedCb.isChecked,
                    priority = getPriority(acb)
                ).let { task ->
                    Intent().apply {
                        putExtra(EXTRA_TASK, task)
                        setResult(RESULT_OK, this)
                    }
                }
                finish()
            }

            // Finaliza a Intent com um resultado cancelado
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

    private fun checkActualPriority(acb: ActivityTaskBinding, task: Task) {
        with(acb) {
            if (task.priority == Priority.LOW) {
                lowPriorityCb.isChecked = true
                midPriorityCb.isChecked = false
                highPriorityCb.isChecked = false
            } else if (task.priority == Priority.MID) {
                lowPriorityCb.isChecked = false
                midPriorityCb.isChecked = true
                highPriorityCb.isChecked = false
            } else {
                lowPriorityCb.isChecked = false
                midPriorityCb.isChecked = false
                highPriorityCb.isChecked = true
            }
        }
    }

    private fun getPriority(acb: ActivityTaskBinding): Priority {
        with(acb) {
            if (lowPriorityCb.isChecked) return Priority.LOW
            else if (midPriorityCb.isChecked) return Priority.MID
            else return Priority.HIGH
        }
    }
}