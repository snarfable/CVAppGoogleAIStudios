package com.example.ui.labs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900

enum class LabTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    LIDAR("LiDAR Ray-Tracing", Icons.Default.Sensors),
    ULTRASONIC("Ultrasonic YOLO", Icons.Default.GraphicEq),
    TELEMETRY("Edge Telemetry", Icons.Default.Bolt)
}

@Composable
fun LabsScreen(modifier: Modifier = Modifier) {
    var activeLab by remember { mutableStateOf(LabTab.LIDAR) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TechNavy900)
            .testTag("labs_screen_root")
    ) {
        // Tab selector row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LabTab.values().forEach { tab ->
                FilterChip(
                    selected = activeLab == tab,
                    onClick = { activeLab = tab },
                    label = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (activeLab == tab) TechNavy900 else CyanNeon
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanNeon,
                        selectedLabelColor = TechNavy900,
                        containerColor = TechNavy800,
                        labelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Active Lab View
        when (activeLab) {
            LabTab.LIDAR -> LidarLabScreen()
            LabTab.ULTRASONIC -> UltrasonicLabScreen()
            LabTab.TELEMETRY -> TelemetryLabScreen()
        }
    }
}
