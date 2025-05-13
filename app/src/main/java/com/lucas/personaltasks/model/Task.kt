package com.lucas.personaltasks.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var title: String = "",
    var description: String = "",
    var limitDateMillis: Long = System.currentTimeMillis()
): Parcelable
