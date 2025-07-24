package com.example.stepdiaryandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.stepdiaryandroid.data.HealthConnectRepository
import com.example.stepdiaryandroid.ui.screen.home.HomeScreen
import com.example.stepdiaryandroid.ui.theme.StepDiaryAndroidTheme
import com.example.stepdiaryandroid.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient

    // 権限リクエストのコールバック登録
    private val requestPermissionsLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(PERMISSIONS)) {
            Log.d("MainActivity", "すべての権限が許可されました")
        } else {
            Log.e("MainActivity", "権限が不足しています")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkHealthConnectAvailability()
    }

    private fun checkHealthConnectAvailability() {
        val context = this
        val providerPackageName = "com.google.android.apps.healthdata"
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, providerPackageName)

        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            Log.e("MainActivity", "Health Connect SDKが利用不可です")
            return
        }

        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            val uriString =
                "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", context.packageName)
                }
            )
            return
        }

        // HealthConnectClient を初期化
        healthConnectClient = HealthConnectClient.getOrCreate(context)

        // ViewModel 作成（Repository → ViewModelFactory経由）
        val repository = HealthConnectRepository(healthConnectClient)
        val viewModelFactory = HomeViewModel.Factory(repository)
        val homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        // 権限チェックを行い、権限がある場合はすでに操作可能
        lifecycleScope.launch {
            checkPermissionsAndRun(healthConnectClient)
        }

        // Compose の画面を表示
        setContent {
            StepDiaryAndroidTheme {
                HomeScreen(homeViewModel)
            }
        }
    }

    private suspend fun checkPermissionsAndRun(client: HealthConnectClient) {
        val granted = client.permissionController.getGrantedPermissions()
        if (!granted.containsAll(PERMISSIONS)) {
            requestPermissionsLauncher.launch(PERMISSIONS)
        } else {
            Log.d("MainActivity", "権限はすでに付与済みです")
        }
    }

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class)
        )
    }
}


