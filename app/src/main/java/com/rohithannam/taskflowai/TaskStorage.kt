package com.rohithannam.taskflowai

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class TaskItem(
    val title: String,
    val done: Boolean = false,
    val priority: String = "Normal"
)

object TaskStorage {
    private const val PREFS = "taskflow_prefs"
    private const val KEY_TASKS = "tasks_json"

    fun read(context: Context): MutableList<TaskItem> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_TASKS, null) ?: return mutableListOf(
                TaskItem("Plan today's priorities", false, "High"),
                TaskItem("Study for 30 minutes"),
                TaskItem("Review completed tasks")
            )
        return try {
            val array = JSONArray(raw)
            MutableList(array.length()) { index ->
                val obj = array.getJSONObject(index)
                TaskItem(
                    title = obj.optString("title"),
                    done = obj.optBoolean("done"),
                    priority = obj.optString("priority", "Normal")
                )
            }
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    fun write(context: Context, tasks: List<TaskItem>) {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(JSONObject().put("title", task.title)
                .put("done", task.done).put("priority", task.priority))
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_TASKS, array.toString()).apply()
    }
}
