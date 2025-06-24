package com.lucas.personaltasks.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale


@Parcelize
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Int? = -1,
    var title: String = "",
    var description: String = "",
    var limitDateMillis: Long = System.currentTimeMillis(),
    var finished: Boolean = false
): Parcelable {
    override fun toString(): String {
        val fmt = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())
        return "$title - $description - ${fmt.format(Date(limitDateMillis))}"
    }
}
