package com.scriptauto.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onOpenScriptList: () -> Unit,
    onNewScript: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("自訂義腳本系統", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(48.dp))
            Button(onClick = onOpenScriptList, modifier = Modifier.fillMaxWidth()) {
                Text("已建立的腳本")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onNewScript, modifier = Modifier.fillMaxWidth()) {
                Text("新建腳本")
            }
        }
    }
}
