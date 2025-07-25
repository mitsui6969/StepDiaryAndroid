package com.example.stepdiaryandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.permission.HealthPermission.Companion.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.stepdiaryandroid.ui.screen.home.HomeScreen
import com.example.stepdiaryandroid.data.HealthConnectRepository
import com.example.stepdiaryandroid.data.worker.ScheduleWorker
import com.example.stepdiaryandroid.ui.theme.StepDiaryAndroidTheme
import com.example.stepdiaryandroid.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var homeViewModel: HomeViewModel

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

        setContent {
            StepDiaryAndroidTheme {
                HomeScreen(homeViewModel)
            }
        }
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

        // HealthConnectClient 初期化
        healthConnectClient = HealthConnectClient.getOrCreate(context)

        // ViewModel 初期化
        val repository = HealthConnectRepository(healthConnectClient)
        val viewModelFactory = HomeViewModel.Factory(repository)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        lifecycleScope.launch {
            val featureStatus = healthConnectClient.features.getFeatureStatus(
                HealthConnectFeatures.FEATURE_READ_HEALTH_DATA_IN_BACKGROUND
            )

            // バックグラウンド処理
            if (featureStatus == HealthConnectFeatures.FEATURE_STATUS_AVAILABLE) {
                val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()

                if (PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND !in grantedPermissions) {
                    Log.d("MainActivity", "バックグラウンド権限なし → Foregroundで実行")
                    // foreground 読み取り処理をここに書く
                } else {
                    Log.d("MainActivity", "バックグラウンド権限あり → WorkManager登録")

                    val periodicWorkRequest = PeriodicWorkRequestBuilder<ScheduleWorker>(
                        1, TimeUnit.HOURS
                    ).build()

                    WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                        "read_health_connect",
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicWorkRequest
                    )
                }
            } else {
                Log.d("MainActivity", "バックグラウンド未対応 → Foregroundで実行")
                // foreground 読み取り処理をここに書く
            }

            checkPermissionsAndRun(healthConnectClient)
        }

    }

    // 権限チェック
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


