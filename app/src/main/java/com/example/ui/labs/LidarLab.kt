package com.example.ui.labs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BlueLaser
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.TechNavy600
import com.example.ui.theme.TechNavy700
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LidarLabScreen(modifier: Modifier = Modifier) {
    // Interactive simulation parameters
    var beamDivergenceMrad by remember { mutableFloatStateOf(1.2f) }
    var surfaceReflectancePercent by remember { mutableFloatStateOf(75f) }
    var scanFrequencyHz by remember { mutableFloatStateOf(20f) }
    var selectedSensorType by remember { mutableStateOf("Velodyne 64") }
    var selectedTarget by remember { mutableStateOf("Rail Track Bed") }

    // Interactive 3D camera rotation state
    var orbitAngleX by remember { mutableFloatStateOf(25f) }
    var orbitAngleY by remember { mutableFloatStateOf(45f) }

    // Live continuous laser sweep pulse
    var sweepPhase by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(scanFrequencyHz) {
        while (true) {
            sweepPhase = (sweepPhase + 0.05f * (scanFrequencyHz / 20f)) % (2 * PI.toFloat())
            delay(33)
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
        // Lab Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TechNavy800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "LiDAR Ray-Tracing Simulator",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Physics-Informed Optical Propagation & 3D Point Cloud",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanNeon
                            )
                        }
                    }
                    IconButton(onClick = {
                        beamDivergenceMrad = 1.2f
                        surfaceReflectancePercent = 75f
                        orbitAngleX = 25f
                        orbitAngleY = 45f
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Simulation", tint = CyanNeon)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Replicating Ryan's multi-threaded C++ ray-tracer authored at Herzog & Raytheon. Simulates beam divergence θ, surface reflectance, and occlusion to synthesize ground-truth point clouds.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive 3D Point Cloud Canvas
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
                        text = "3D POINT CLOUD & RAY PROPAGATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyanNeon
                    )
                    Text(
                        text = "Drag canvas to orbit 3D",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TechNavy900)
                        .border(1.dp, TechNavy700, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                orbitAngleY = (orbitAngleY + dragAmount.x * 0.4f) % 360f
                                orbitAngleX = (orbitAngleX - dragAmount.y * 0.4f).coerceIn(-10f, 60f)
                            }
                        }
                        .testTag("lidar_canvas_box")
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2f
                        val centerY = size.height * 0.58f

                        // Draw background spatial grid
                        val gridCols = 8
                        val gridRows = 6
                        val gridSpacingX = size.width / gridCols
                        val gridSpacingY = size.height / gridRows
                        for (i in 0..gridCols) {
                            drawLine(
                                color = TechNavy700.copy(alpha = 0.4f),
                                start = Offset(i * gridSpacingX, 0f),
                                end = Offset(i * gridSpacingX, size.height),
                                strokeWidth = 0.8f
                            )
                        }
                        for (j in 0..gridRows) {
                            drawLine(
                                color = TechNavy700.copy(alpha = 0.4f),
                                start = Offset(0f, j * gridSpacingY),
                                end = Offset(size.width, j * gridSpacingY),
                                strokeWidth = 0.8f
                            )
                        }

                        // Perspective rotation transform
                        val radY = (orbitAngleY * PI / 180f).toFloat()
                        val radX = (orbitAngleX * PI / 180f).toFloat()

                        // Project 3D point (x, y, z) to 2D
                        fun project(x: Float, y: Float, z: Float): Offset {
                            // Rotate around Y axis
                            val rx = x * cos(radY) - z * sin(radY)
                            val rz = x * sin(radY) + z * cos(radY)
                            // Tilt with X axis
                            val ry = y * cos(radX) - rz * sin(radX)
                            val scale = 1.0f + (rz / 400f)
                            return Offset(centerX + rx * scale, centerY + ry * scale)
                        }

                        // Draw simulated rail track beds & ground geometry
                        val railLength = 220f
                        val railWidth = 70f
                        val p1 = project(-railWidth, 50f, -railLength)
                        val p2 = project(railWidth, 50f, -railLength)
                        val p3 = project(railWidth, 50f, railLength)
                        val p4 = project(-railWidth, 50f, railLength)

                        val trackPath = Path().apply {
                            moveTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            lineTo(p3.x, p3.y)
                            lineTo(p4.x, p4.y)
                            close()
                        }
                        drawPath(
                            path = trackPath,
                            color = TechNavy800.copy(alpha = 0.8f)
                        )
                        drawPath(
                            path = trackPath,
                            color = TechNavy600,
                            style = Stroke(width = 1.5f)
                        )

                        // Draw dual steel rails
                        val leftRailP1 = project(-railWidth * 0.6f, 40f, -railLength)
                        val leftRailP2 = project(-railWidth * 0.6f, 40f, railLength)
                        val rightRailP1 = project(railWidth * 0.6f, 40f, -railLength)
                        val rightRailP2 = project(railWidth * 0.6f, 40f, railLength)
                        drawLine(CyanNeon.copy(alpha = 0.5f), leftRailP1, leftRailP2, strokeWidth = 3f)
                        drawLine(CyanNeon.copy(alpha = 0.5f), rightRailP1, rightRailP2, strokeWidth = 3f)

                        // Draw LiDAR sensor emitter origin
                        val sensorOrigin = project(0f, -70f, 0f)
                        drawCircle(
                            color = CyanNeon,
                            radius = 6f,
                            center = sensorOrigin
                        )
                        drawCircle(
                            color = CyanNeon.copy(alpha = 0.3f),
                            radius = 12f,
                            center = sensorOrigin,
                            style = Stroke(1.5f)
                        )

                        // Generate synthetic LiDAR point cloud points with beam divergence spot expansion
                        val numRays = if (selectedSensorType.contains("64")) 48 else 28
                        for (r in 0 until numRays) {
                            val angle = (r * (360f / numRays) + sweepPhase * 57.3f) * (PI / 180f).toFloat()
                            val distance = 110f + (r % 5) * 18f
                            val targetX = sin(angle) * distance
                            val targetZ = cos(angle) * distance
                            val targetY = 48f + sin(r.toFloat()) * 4f

                            val hitPoint = project(targetX, targetY, targetZ)

                            // Draw ray pulse
                            val isNearSweep = (r % 8 == 0)
                            if (isNearSweep) {
                                drawLine(
                                    brush = Brush.linearGradient(
                                        colors = listOf(CyanNeon.copy(alpha = 0.8f), BlueLaser.copy(alpha = 0.2f))
                                    ),
                                    start = sensorOrigin,
                                    end = hitPoint,
                                    strokeWidth = (beamDivergenceMrad * 0.8f).coerceIn(0.8f, 3.5f)
                                )
                            }

                            // Beam divergence spot expansion at distance
                            val spotRadius = (beamDivergenceMrad * 1.5f + (distance / 50f)).coerceIn(2.5f, 9f)

                            // Point return intensity color
                            val intensity = (surfaceReflectancePercent / 100f) * (1f - (distance / 240f)).coerceIn(0.3f, 1f)
                            val pointColor = when {
                                intensity > 0.75f -> CyanNeon
                                intensity > 0.5f -> EmeraldTelemetry
                                intensity > 0.3f -> AmberSignal
                                else -> BlueLaser
                            }

                            drawCircle(
                                color = pointColor.copy(alpha = intensity),
                                radius = spotRadius,
                                center = hitPoint
                            )
                        }

                        // Volumetric bounding box overlay
                        val boxP1 = project(-30f, 10f, -40f)
                        val boxP2 = project(30f, 10f, -40f)
                        val boxP3 = project(30f, 48f, -40f)
                        val boxP4 = project(-30f, 48f, -40f)
                        val boxPath = Path().apply {
                            moveTo(boxP1.x, boxP1.y)
                            lineTo(boxP2.x, boxP2.y)
                            lineTo(boxP3.x, boxP3.y)
                            lineTo(boxP4.x, boxP4.y)
                            close()
                        }
                        drawPath(boxPath, AmberSignal.copy(alpha = 0.2f))
                        drawPath(boxPath, AmberSignal, style = Stroke(1.2f))
                    }

                    // On-canvas real-time status overlay
                    Surface(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.BottomStart),
                        shape = RoundedCornerShape(6.dp),
                        color = TechNavy900.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechNavy700)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(
                                text = "Spot Size w(z) = ${(beamDivergenceMrad * 12.4f).toInt()} mm",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = CyanNeon
                            )
                            Text(
                                text = "Est. Ballast Vol = 38.4 m³ (±0.6%)",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = EmeraldTelemetry
                            )
                        }
                    }
                }
            }
        }

        // Live Benchmark Telemetry KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LidarMetricCard(
                title = "Ray Throughput",
                value = "${(scanFrequencyHz * 128000 / 1000).toInt()}k",
                unit = "rays/sec",
                color = CyanNeon,
                modifier = Modifier.weight(1f)
            )
            LidarMetricCard(
                title = "Spot Expansion",
                value = String.format("%.2f", beamDivergenceMrad * 1.8f),
                unit = "mm @ 15m",
                color = AmberSignal,
                modifier = Modifier.weight(1f)
            )
            LidarMetricCard(
                title = "Return SNR",
                value = "${(surfaceReflectancePercent * 0.42f).toInt()}",
                unit = "dB",
                color = EmeraldTelemetry,
                modifier = Modifier.weight(1f)
            )
        }

        // Parameter Controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TechNavy800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OPTICAL & SENSOR CONTROLS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CyanNeon
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Beam Divergence Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Beam Divergence (θ)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${String.format("%.1f", beamDivergenceMrad)} mrad",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = CyanNeon
                    )
                }
                Slider(
                    value = beamDivergenceMrad,
                    onValueChange = { beamDivergenceMrad = it },
                    valueRange = 0.4f..3.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanNeon,
                        activeTrackColor = CyanNeon,
                        inactiveTrackColor = TechNavy600
                    )
                )

                // Surface Reflectance Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Target Surface Reflectance (ρ)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${surfaceReflectancePercent.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = EmeraldTelemetry
                    )
                }
                Slider(
                    value = surfaceReflectancePercent,
                    onValueChange = { surfaceReflectancePercent = it },
                    valueRange = 10f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = EmeraldTelemetry,
                        activeTrackColor = EmeraldTelemetry,
                        inactiveTrackColor = TechNavy600
                    )
                )

                // Scan Frequency Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Scan Rotation Frequency", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${scanFrequencyHz.toInt()} Hz",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = AmberSignal
                    )
                }
                Slider(
                    value = scanFrequencyHz,
                    onValueChange = { scanFrequencyHz = it },
                    valueRange = 10f..50f,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberSignal,
                        activeTrackColor = AmberSignal,
                        inactiveTrackColor = TechNavy600
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sensor Hardware Selection Chips
                Text(
                    text = "Sensor Array Architecture:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Velodyne 64", "Solid-State", "Custom Rail Array").forEach { sensor ->
                        FilterChip(
                            selected = selectedSensorType == sensor,
                            onClick = { selectedSensorType = sensor },
                            label = { Text(sensor, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = TechNavy900,
                                containerColor = TechNavy700,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LidarMetricCard(
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = color
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
