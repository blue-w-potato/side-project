package com.scriptauto.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.scriptauto.app.data.ScriptRecord
import com.scriptauto.app.data.ScriptRepository

@Composable
fun ScriptListScreen(
    repository: ScriptRepository,
    onBack: () -> Unit,
    onEdit: (ScriptRecord) -> Unit,
) {
    val vm: ScriptListViewModel = viewModel(factory = ScriptListViewModelFactory(repository))
    val scripts by vm.scripts.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("已建立的腳本") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (scripts.isEmpty()) {
                Text(
                    "尚未建立腳本",
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn {
                    items(scripts, key = { it.id }) { script ->
                        ScriptRow(
                            script = script,
                            onDelete = { vm.requestDelete(script) },
                            onEdit = { vm.tryEdit(script) { onEdit(script) } },
                            onStart = { vm.tryStart(script, context) },
                        )
                        Divider()
                    }
                }
            }
        }
    }

    // 刪除確認(原始規格)
    vm.deleteTarget.value?.let { target ->
        AlertDialog(
            onDismissRequest = { vm.cancelDelete() },
            title = { Text("刪除腳本") },
            text = { Text("確定要刪除「${target.name}」嗎?") },
            confirmButton = { TextButton(onClick = { vm.confirmDelete() }) { Text("確定") } },
            dismissButton = { TextButton(onClick = { vm.cancelDelete() }) { Text("取消") } },
        )
    }

    // Q9 / Q22 / Q26 擋下時的提示
    vm.blockedMessage.value?.let { message ->
        AlertDialog(
            onDismissRequest = { vm.dismissBlockedMessage() },
            title = { Text("無法執行") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { vm.dismissBlockedMessage() }) { Text("知道了") } },
        )
    }
}

@Composable
private fun ScriptRow(
    script: ScriptRecord,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onStart: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(script.name, modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "刪除") }
        IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "編輯") }
        IconButton(onClick = onStart) { Icon(Icons.Default.PlayArrow, contentDescription = "啟動") }
    }
}
