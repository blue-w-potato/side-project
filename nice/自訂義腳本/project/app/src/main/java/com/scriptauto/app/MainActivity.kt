package com.scriptauto.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.scriptauto.app.data.ScriptOrientation
import com.scriptauto.app.ui.EditorScreen
import com.scriptauto.app.ui.HomeScreen
import com.scriptauto.app.ui.NewScriptOrientationScreen
import com.scriptauto.app.ui.Screen

class MainActivity : ComponentActivity() {

    private var hasRequestedPermissionsOnce = false
    private val permissionsGranted = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                if (permissionsGranted.value) {
                    AppNavHost(repository = (application as ScriptAutoApp).repository)
                } else {
                    PermissionPendingScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        permissionsGranted.value = PermissionGate.allGranted(this)
        if (permissionsGranted.value) return

        if (!hasRequestedPermissionsOnce) {
            hasRequestedPermissionsOnce = true
            if (!PermissionGate.isAccessibilityServiceEnabled(this)) {
                startActivity(PermissionGate.accessibilitySettingsIntent())
                return
            }
            if (!PermissionGate.canDrawOverlays(this)) {
                startActivity(PermissionGate.overlaySettingsIntent(this))
                return
            }
        } else {
            // 使用者從設定頁回來,但權限仍未齊全 => 視為拒絕,直接結束程式(Q23)
            finish()
        }
    }
}

@Composable
private fun PermissionPendingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("請授予無障礙服務與懸浮視窗權限")
        }
    }
}

@Composable
private fun AppNavHost(
    repository: com.scriptauto.app.data.ScriptRepository,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onOpenScriptList = { navController.navigate(Screen.ScriptList.route) },
                onNewScript = { navController.navigate(Screen.NewScriptOrientation.route) },
            )
        }
        composable(Screen.NewScriptOrientation.route) {
            NewScriptOrientationScreen(
                onOrientationChosen = { orientation ->
                    navController.navigate(Screen.Editor.buildRoute(orientation.name))
                },
            )
        }
        composable(Screen.ScriptList.route) {
            com.scriptauto.app.ui.ScriptListScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onEdit = { script ->
                    navController.navigate(Screen.Editor.buildRoute(script.orientation.name, script.id))
                },
            )
        }
        composable(
            route = Screen.Editor.route,
            arguments = listOf(
                navArgument("orientation") { type = NavType.StringType },
                navArgument("scriptId") { type = NavType.StringType; nullable = true; defaultValue = null },
            ),
        ) { backStackEntry ->
            val orientation = ScriptOrientation.valueOf(
                backStackEntry.arguments?.getString("orientation") ?: ScriptOrientation.PORTRAIT.name,
            )
            val scriptId = backStackEntry.arguments?.getString("scriptId")?.toLongOrNull()
            EditorScreen(
                orientation = orientation,
                scriptId = scriptId,
                repository = repository,
                onLeave = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
            )
        }
    }
}

