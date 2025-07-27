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
import com.example.stepdiaryandroid.data.UserRepository
import com.example.stepdiaryandroid.data.worker.ScheduleWorker
import com.example.stepdiaryandroid.ui.screen.AppScreen
import com.example.stepdiaryandroid.ui.theme.StepDiaryAndroidTheme
import com.example.stepdiaryandroid.viewmodel.HomeViewModel
import com.example.stepdiaryandroid.viewmodel.SettingViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingViewModel

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

        lifecycleScope.launch {
            val available = checkHealthConnectAvailability()
            if (!available) return@launch

            checkPermissionsAndRun()

            // ViewModel 初期化
            val repository = HealthConnectRepository(healthConnectClient)
            val userRepository = UserRepository(applicationContext)

            val homeViewModelFactory = HomeViewModel.Factory(repository, userRepository)
            homeViewModel = ViewModelProvider(this@MainActivity, homeViewModelFactory)[HomeViewModel::class.java]

            val settingsViewModelFactory = SettingViewModel.Factory(userRepository)
            settingsViewModel = ViewModelProvider(this@MainActivity, settingsViewModelFactory)[SettingViewModel::class.java]

            val (start, end) = homeViewModel.getTodayTimeRange()
            homeViewModel.loadSteps(start, end)

            // UI構築
            setContent {
                StepDiaryAndroidTheme {
                    AppScreen(homeViewModel, settingsViewModel)
                }
            }

            // WorkManager登録（バックグラウンド対応チェック含む）
            scheduleBackgroundWorkIfNeeded()
        }
    }

    // Health Connect の利用可否と初期化
    private suspend fun checkHealthConnectAvailability(): Boolean {
        val providerPackageName = "com.google.android.apps.healthdata"
        val status = HealthConnectClient.getSdkStatus(this, providerPackageName)

        when (status) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                Log.e("MainActivity", "Health Connect SDKが利用不可です")
                return false
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                val uri = Uri.parse("market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding")
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = uri
                    putExtra("overlay", true)
                    putExtra("callerId", packageName)
                })
                return false
            }
        }

        healthConnectClient = HealthConnectClient.getOrCreate(this)
        return true
    }

    // 権限チェックとリクエスト
    private suspend fun checkPermissionsAndRun() {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (!granted.containsAll(PERMISSIONS)) {
            requestPermissionsLauncher.launch(PERMISSIONS)
        } else {
            Log.d("MainActivity", "権限はすでに付与済みです")
        }
    }

    // バックグラウンド読み取り処理の登録
    private suspend fun scheduleBackgroundWorkIfNeeded() {
        val status = healthConnectClient.features.getFeatureStatus(
            HealthConnectFeatures.FEATURE_READ_HEALTH_DATA_IN_BACKGROUND
        )

        if (status != HealthConnectFeatures.FEATURE_STATUS_AVAILABLE) {
            Log.d("MainActivity", "バックグラウンド未対応 → Foregroundで実行")
            return
        }

        val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
        if (PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND !in grantedPermissions) {
            Log.d("MainActivity", "バックグラウンド権限なし → Foregroundで実行")
            return
        }

        Log.d("MainActivity", "バックグラウンド権限あり → WorkManager登録")

        val request = PeriodicWorkRequestBuilder<ScheduleWorker>(
            1, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "read_health_connect",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class)
        )
    }
}
