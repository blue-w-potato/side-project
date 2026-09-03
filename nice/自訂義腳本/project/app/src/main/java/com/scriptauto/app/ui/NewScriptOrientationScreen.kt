package com.scriptauto.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scriptauto.app.data.ScriptOrientation

@Composable
fun NewScriptOrientationScreen(
    onOrientationChosen: (ScriptOrientation) -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("選擇編排方向(選定後無法在編排中切換)")
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { onOrientationChosen(ScriptOrientation.LANDSCAPE) }) {
                    Text("橫式")
                }
                Button(onClick = { onOrientationChosen(ScriptOrientation.PORTRAIT) }) {
                    Text("直式")
                }
            }
        }
    }
}
