package com.lucas.personaltasks.ui

interface OnHistoryTaskClickener {
    // Funções de clique longo nas tasks do histórico
    fun onReactivateTaskMenuItemClick(position: Int)
    fun onDetailTaskMenuItemClick(position: Int)
}