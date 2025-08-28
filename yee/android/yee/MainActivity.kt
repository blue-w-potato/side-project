package com.example.blankapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.blankapp.ui.theme.BlankAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlankAppTheme {
                // 創建一個完全空白的白色畫面
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    // 這裡是空的，什麼都沒有
                }
            }
        }
    }
}

@Composable
fun BlankScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        // 空白畫面，沒有任何內容
    }
}

@Preview(showBackground = true)
@Composable
fun BlankScreenPreview() {
    BlankAppTheme {
        BlankScreen()
    }
}