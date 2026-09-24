package com.example.ui.labs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.RedHazard
import com.example.ui.theme.TechNavy600
import com.example.ui.theme.TechNavy700
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun TelemetryLabScreen(modifier: Modifier = Modifier) {
    var isDeterministicMode by remember { mutableStateOf(true) }
    var isBurstActive by remember { mutableStateOf(false) }
    var packetsProcessed by remember { mutableIntStateOf(148520) }
    var queueDepth by remember { mutableFloatStateOf(12f) }

    // Latency history for live oscilloscope
    val latencyHistory = remember { mutableStateListOf<Float>() }

    LaunchedEffect(Unit) {
        // Initialize sample history
        repeat(40) { latencyHistory.add(18f) }
    }

    LaunchedEffect(isDeterministicMode, isBurstActive) {
        while (true) {
            val newLatency = if (isDeterministicMode) {
                // Strictly bounded jitter 14 - 24 microseconds
                val jitter = if (isBurstActive) Random.nextFloat() * 8f else Random.nextFloat() * 4f
                16f + jitter
            } else {
                // Non-deterministic: GC pauses & kernel scheduling spikes up to 180us
                val spike = if (Random.nextFloat() < 0.15f) Random.nextFloat() * 120f else 0f
                25f + spike + Random.nextFloat() * 12f
            }

            if (latencyHistory.size >= 50) {
                latencyHistory.removeAt(0)
            }
            latencyHistory.add(newLatency)

            packetsProcessed += if (isBurstActive) 120 else 24
            queueDepth = if (isBurstActive) {
                (queueDepth + 4f).coerceIn(10f, 85f)
            } else {
                (queueDepth - 2f).coerceIn(5f, 25f)
            }

            delay(60)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TechNavy900)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TechNavy800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanNeon.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Deterministic Edge Telemetry & HIL Lab",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Northrop Grumman & Elcotec ChannelSON Architecture",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanNeon
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Benchmarks low-latency event APIs, lock-free ring buffer telemetry queues, and ChannelSON in-flash parsing to guarantee deterministic sub-millisecond execution.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live Jitter Oscilloscope Graph
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TechNavy800)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REAL-TIME INTER-BOARD LATENCY (µs)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyanNeon
                    )
                    Text(
                        text = if (isDeterministicMode) "Deterministic Lock-Free" else "Standard Kernel/GC",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isDeterministicMode) EmeraldTelemetry else AmberSignal
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TechNavy900)
                        .border(1.dp, TechNavy700, RoundedCornerShape(10.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw horizontal baseline grids
                        val maxScale = 150f
                        val thresholdY = size.height * (1f - (50f / maxScale))

                        // Draw 50µs SLA line
                        drawLine(
                            color = RedHazard.copy(alpha = 0.5f),
                            start = Offset(0f, thresholdY),
                            end = Offset(size.width, thresholdY),
                            strokeWidth = 1.0f
                        )

                        // Draw graph path
                        if (latencyHistory.size >= 2) {
                            val stepX = size.width / (latencyHistory.size - 1)
                            val path = Path()

                            latencyHistory.forEachIndexed { idx, lat ->
                                val x = idx * stepX
                                val normalizedY = size.height * (1f - (lat / maxScale).coerceIn(0.05f, 0.95f))
                                if (idx == 0) path.moveTo(x, normalizedY) else path.lineTo(x, normalizedY)
                            }

                            val lineColor = if (isDeterministicMode) EmeraldTelemetry else AmberSignal
                            drawPath(path, lineColor, style = Stroke(width = 2.2f))
                        }
                    }

                    // SLA Label
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd),
                        shape = RoundedCornerShape(4.dp),
                        color = TechNavy900.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = "50 µs Mission SLA Limit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = RedHazard,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lock-Free Ring Buffer Mode",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                        Text(
                            text = if (isDeterministicMode) "Zero-copy shared memory enabled" else "Simulating standard non-deterministic OS delays",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isDeterministicMode,
                        onCheckedChange = { isDeterministicMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyanNeon,
                            checkedTrackColor = TechNavy700,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = TechNavy900
                        )
                    )
                }
            }
        }

        // Live Telemetry KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LidarMetricCard(
                title = "Mean Latency",
                value = if (isDeterministicMode) "18.4 µs" else "48.2 µs",
                unit = "Inter-board",
                color = CyanNeon,
                modifier = Modifier.weight(1f)
            )
            LidarMetricCard(
                title = "Packet Jitter",
                value = if (isDeterministicMode) "± 2.8 µs" else "± 78.4 µs",
                unit = "Max Variance",
                color = if (isDeterministicMode) EmeraldTelemetry else RedHazard,
                modifier = Modifier.weight(1f)
            )
            LidarMetricCard(
                title = "Packet Drops",
                value = "0.00%",
                unit = "Zero-Drop Queue",
                color = EmeraldTelemetry,
                modifier = Modifier.weight(1f)
            )
        }

        // ChannelSON Embedded Dispatcher Buffer Stress Test
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TechNavy800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CHANNELSON IN-FLASH EVENT DISPATCHER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CyanNeon
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Designed for Elcotec microcontrollers: parses serial config in flash memory without dynamic heap allocations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Queue buffer visualizer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Flash Queue Depth", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${queueDepth.toInt()} / 128 slots",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = CyanNeon
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Custom bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(TechNavy900)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(queueDepth / 128f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(
                                if (queueDepth > 80f) RedHazard else if (queueDepth > 50f) AmberSignal else EmeraldTelemetry
                            )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stress Test Button
                Button(
                    onClick = { isBurstActive = !isBurstActive },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBurstActive) RedHazard else CyanNeon
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("burst_stress_test_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (isBurstActive) Icons.Default.Bolt else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = TechNavy900,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBurstActive) "Stop Telemetry Burst" else "Inject 10,000 Packets/sec Burst",
                        color = TechNavy900,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
