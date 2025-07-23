package com.example.stepdiaryandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.lifecycleScope
import com.example.stepdiaryandroid.ui.theme.StepDiaryAndroidTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var healthConnectClient: HealthConnectClient

    // 権限リクエストのコールバック登録
    private val requestPermissionsLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(PERMISSIONS)) {
            Log.d("MainActivity", "すべての権限が許可されました")
            // 権限取得後の処理をここに書くか、別関数で呼ぶ
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
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
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", context.packageName)
                }
            )
            return
        }

        healthConnectClient = HealthConnectClient.getOrCreate(context)

        lifecycleScope.launch {
            checkPermissionsAndRun(healthConnectClient)
        }
    }

    private suspend fun checkPermissionsAndRun(client: HealthConnectClient) {
        val granted = client.permissionController.getGrantedPermissions()
        if (!granted.containsAll(PERMISSIONS)) {
            requestPermissionsLauncher.launch(PERMISSIONS)
        } else {
            Log.d("MainActivity", "権限はすでに付与済みです")
            // ここでHealth Connectの読み書き処理を呼ぶ
        }
    }

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StepDiaryAndroidTheme {
        Greeting("Android")
    }
}
