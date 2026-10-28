package com.rohithannam.taskflowai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rohithannam.taskflowai.widget.TaskFlowWidget
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TaskFlowApp() }
    }
}

@Composable
private fun TaskFlowApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tasks = remember { mutableStateListOf<TaskItem>().apply { addAll(TaskStorage.read(context)) } }
    var draft by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val completed = tasks.count { it.done }
    val progress = if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size
    val scope = rememberCoroutineScope()

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Text("TASKFLOW AI", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("Make today count.", style = MaterialTheme.typography.headlineMedium)
                Text("$completed of ${tasks.size} tasks complete",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Add a task") },
                    singleLine = true
                )
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    val title = TaskTextValidator.normalize(draft)
                    if (title == null) {
                        error = "Enter a task first."
                    } else {
                        tasks.add(0, TaskItem(title))
                        draft = ""
                        persistAndRefresh(context, tasks.toList())
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("＋ Add task") }
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(tasks, key = { index, task -> "$index-${task.title}" }) { index, task ->
                        Card(shape = RoundedCornerShape(14.dp)) {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = task.done, onCheckedChange = { checked ->
                                    tasks[index] = task.copy(done = checked)
                                    persistAndRefresh(context, tasks.toList())
                                })
                                Column(Modifier.weight(1f)) {
                                    Text(task.title, textDecoration =
                                        if (task.done) TextDecoration.LineThrough else TextDecoration.None)
                                    if (task.priority == "High" && !task.done) {
                                        Text("HIGH PRIORITY", style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                TextButton(onClick = {
                                    tasks.removeAt(index)
                                    persistAndRefresh(context, tasks.toList())
                                }) { Text("Delete") }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Tip: add the TaskFlow widget from your home-screen widget picker.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun persistAndRefresh(context: android.content.Context, tasks: List<TaskItem>) {
    TaskStorage.write(context, tasks)
    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
        TaskFlowWidget().updateAll(context)
    }
}
