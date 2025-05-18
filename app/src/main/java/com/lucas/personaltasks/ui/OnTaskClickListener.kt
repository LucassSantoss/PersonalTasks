package com.lucas.personaltasks.ui

interface OnTaskClickListener {
    fun onTaskClick(position: Int)
    fun onRemoveTaskMenuItemClick(position: Int)
    fun onEditTaskMenuItemClick(position: Int)
    fun onDetailTaskMenuItemClick(position: Int)
}