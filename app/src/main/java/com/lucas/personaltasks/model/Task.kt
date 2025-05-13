package com.lucas.personaltasks.model

import android.os.Parcelable
import java.time.LocalDate

data class Task(
    var title: String = "",
    var description: String = "",
    var limitDate: LocalDate = LocalDate.now()
)
