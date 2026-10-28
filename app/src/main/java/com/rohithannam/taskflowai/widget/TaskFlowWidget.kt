package com.rohithannam.taskflowai.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionRunCallback
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.unit.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.action.clickable
import androidx.glance.text.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohithannam.taskflowai.MainActivity
import com.rohithannam.taskflowai.TaskStorage
import com.rohithannam.taskflowai.TaskItem
import org.json.JSONArray

class TaskFlowWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val ctx = LocalContext.current
            val tasks = TaskStorage.read(ctx).take(5)
            val done = tasks.count { it.done }
            Column(
                modifier = GlanceModifier.fillMaxSize().background(Color(0xFF151821))
                    .cornerRadius(18.dp).padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TASKFLOW AI", style = TextStyle(color = ColorProvider(Color(0xFF9FA8FF)), fontSize = 12.sp, fontWeight = FontWeight.Bold))
                    Spacer(GlanceModifier.defaultWeight())
                    Text("$done/${tasks.size}", style = TextStyle(color = ColorProvider(Color.White), fontSize = 12.sp))
                }
                Spacer(GlanceModifier.height(6.dp))
                if (tasks.isEmpty()) {
                    Text("No tasks yet — open the app to add one.", style = TextStyle(color = ColorProvider(Color.White), fontSize = 12.sp))
                } else {
                    tasks.forEachIndexed { index, task ->
                        Row(
                            modifier = GlanceModifier.fillMaxWidth().padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (task.done) "☑" else "□",
                                modifier = GlanceModifier.clickable(
                                    actionRunCallback<ToggleTaskAction>(
                                        actionParametersOf(TaskIndexKey to index)
                                    )
                                ),
                                style = TextStyle(color = ColorProvider(Color(0xFF9FA8FF)), fontSize = 18.sp)
                            )
                            Spacer(GlanceModifier.width(8.dp))
                            Text(
                                task.title,
                                modifier = GlanceModifier.defaultWeight(),
                                style = TextStyle(
                                    color = ColorProvider(if (task.done) Color(0xFF8B909C) else Color.White),
                                    fontSize = 12.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
                Spacer(GlanceModifier.height(4.dp))
                Text("Tap a box to complete a task", style = TextStyle(color = ColorProvider(Color(0xFF9AA0AE)), fontSize = 10.sp))
            }
        }
    }
}

class TaskFlowWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskFlowWidget()
}

private val TaskIndexKey = ActionParameters.Key<Int>("task_index")

class ToggleTaskAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val index = parameters[TaskIndexKey] ?: return
        val tasks = TaskStorage.read(context)
        if (index in tasks.indices) {
            tasks[index] = tasks[index].copy(done = !tasks[index].done)
            TaskStorage.write(context, tasks)
            TaskFlowWidget().updateAll(context)
        }
    }
}
