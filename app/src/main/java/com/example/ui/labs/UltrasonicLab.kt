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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BlueLaser
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.RedHazard
import com.example.ui.theme.TechNavy600
import com.example.ui.theme.TechNavy700
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin

@Composable
fun UltrasonicLabScreen(modifier: Modifier = Modifier) {
    var probePositionCm by remember { mutableFloatStateOf(48f) }
    var confidenceThreshold by remember { mutableFloatStateOf(0.85f) }
    var selectedDefectType by remember { mutableStateOf("Transverse Fissure") }

    // Calculate defect detection state based on probe position
    // Defect is located at position 45cm - 55cm
    val defectCenter = 50f
    val distanceFromDefect = abs(probePositionCm - defectCenter)
    val isDefectDetected = distanceFromDefect < 6.5f
    val detectionConfidence = if (isDefectDetected) {
        (0.98f - (distanceFromDefect * 0.035f)).coerceIn(0.70f, 0.99f)
    } else {
        0.12f
    }
    val meetsThreshold = detectionConfidence >= confidenceThreshold

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanNeon.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Ultrasonic Anomaly Recognition Engine",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Custom YOLO-style CNN for Acoustic Signature Inspection",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanNeon
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Adapted YOLO convolutional feature extractors to 2D ultrasonic A-Scan / B-Scan spectrogram tensors, automating real-time rail flaw detection along track infrastructure.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Rail Track & Transducer Probe Interactive Section
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
                        text = "RAIL PROFILE & TRANSDUCER PROBE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyanNeon
                    )
                    Text(
                        text = "Position: ${probePositionCm.toInt()} cm",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (meetsThreshold) RedHazard else EmeraldTelemetry
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Canvas showing Rail Cross Section and Ultrasonic beam penetration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TechNavy900)
                        .border(1.dp, TechNavy700, RoundedCornerShape(10.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val railY = size.height * 0.35f
                        val railHeight = size.height * 0.55f

                        // Draw continuous rail track profile
                        drawRoundRect(
                            color = TechNavy700,
                            topLeft = Offset(16f, railY),
                            size = Size(size.width - 32f, railHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                        drawRoundRect(
                            color = TechNavy600,
                            topLeft = Offset(16f, railY),
                            size = Size(size.width - 32f, railHeight),
                            cornerRadius = CornerRadius(8f, 8f),
                            style = Stroke(1.5f)
                        )

                        // Draw Internal Defect Region in the Rail
                        val defectPixelX = 16f + (defectCenter / 100f) * (size.width - 32f)
                        val defectPixelY = railY + railHeight * 0.45f
                        drawCircle(
                            color = RedHazard.copy(alpha = 0.8f),
                            radius = 12f,
                            center = Offset(defectPixelX, defectPixelY)
                        )
                        drawCircle(
                            color = RedHazard,
                            radius = 16f,
                            center = Offset(defectPixelX, defectPixelY),
                            style = Stroke(1.2f)
                        )

                        // Draw Transducer Probe on top of the rail
                        val probePixelX = 16f + (probePositionCm / 100f) * (size.width - 32f)
                        val probeWidth = 28f
                        val probeHeight = 22f

                        // Transducer box
                        drawRoundRect(
                            color = CyanNeon,
                            topLeft = Offset(probePixelX - probeWidth / 2f, railY - probeHeight),
                            size = Size(probeWidth, probeHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )

                        // Ultrasonic beam cone firing into the rail steel
                        val beamPath = Path().apply {
                            moveTo(probePixelX, railY)
                            lineTo(probePixelX - 26f, railY + railHeight)
                            lineTo(probePixelX + 26f, railY + railHeight)
                            close()
                        }
                        val beamColor = if (meetsThreshold) RedHazard else CyanNeon
                        drawPath(
                            path = beamPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    beamColor.copy(alpha = 0.5f),
                                    beamColor.copy(alpha = 0.05f)
                                )
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive slider for moving transducer probe
                Text(
                    text = "Drag probe along rail axis (Defect located near 50cm):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Slider(
                    value = probePositionCm,
                    onValueChange = { probePositionCm = it },
                    valueRange = 5f..95f,
                    colors = SliderDefaults.colors(
                        thumbColor = if (meetsThreshold) RedHazard else CyanNeon,
                        activeTrackColor = if (meetsThreshold) RedHazard else CyanNeon,
                        inactiveTrackColor = TechNavy600
                    ),
                    modifier = Modifier.testTag("ultrasonic_probe_slider")
                )
            }
        }

        // Live A-Scan Oscilloscope & B-Scan Spectrogram
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
                        text = "1D A-SCAN ECHO OSCILLOSCOPE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyanNeon
                    )
                    Text(
                        text = "Time of Flight (µs)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Oscilloscope Waveform Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TechNavy900)
                        .border(1.dp, TechNavy700, RoundedCornerShape(8.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val midY = size.height * 0.6f

                        // Draw oscilloscope grid lines
                        for (i in 1..4) {
                            val y = size.height * (i / 5f)
                            drawLine(TechNavy700.copy(alpha = 0.5f), Offset(0f, y), Offset(size.width, y), 0.8f)
                        }

                        // Generate synthetic acoustic echo pulse train
                        // Pulse 1: Surface entry pulse at t = 10%
                        // Pulse 2: Flaw anomaly reflection at t = 45% (amplitude proportional to defect detection)
                        // Pulse 3: Backwall base echo at t = 85%
                        val wavePath = Path()
                        val numSamples = 200
                        val flawAmp = if (isDefectDetected) detectionConfidence * 0.75f else 0.05f

                        for (s in 0 until numSamples) {
                            val t = s.toFloat() / numSamples
                            val x = t * size.width

                            // Surface echo (sharp burst around t = 0.12)
                            val surfaceEcho = exp(-((t - 0.12f) * (t - 0.12f)) / 0.0003f) * sin(t * 180f) * 0.6f
                            // Flaw echo (burst around t = 0.45)
                            val defectEcho = exp(-((t - 0.45f) * (t - 0.45f)) / 0.0005f) * sin(t * 160f) * flawAmp
                            // Backwall echo (burst around t = 0.82)
                            val backwallEcho = exp(-((t - 0.82f) * (t - 0.82f)) / 0.0004f) * sin(t * 170f) * 0.55f

                            val netAmplitude = surfaceEcho + defectEcho + backwallEcho
                            val y = midY - netAmplitude * (size.height * 0.42f)

                            if (s == 0) wavePath.moveTo(x, y) else wavePath.lineTo(x, y)
                        }

                        val strokeColor = if (meetsThreshold) RedHazard else CyanNeon
                        drawPath(wavePath, strokeColor, style = Stroke(width = 2.0f))

                        // If flaw echo detected, annotate with bounding indicator
                        if (meetsThreshold) {
                            val flawX = 0.45f * size.width
                            drawLine(
                                color = RedHazard.copy(alpha = 0.7f),
                                start = Offset(flawX, 10f),
                                end = Offset(flawX, size.height - 10f),
                                strokeWidth = 1.2f
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Neural Network YOLO Anomaly Classification Output
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = if (meetsThreshold) RedHazard.copy(alpha = 0.15f) else EmeraldTelemetry.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (meetsThreshold) RedHazard else EmeraldTelemetry
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (meetsThreshold) "ANOMALY DETECTED [YOLO CLASS 0]" else "NOMINAL STEEL STRUCTURE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = if (meetsThreshold) RedHazard else EmeraldTelemetry
                            )
                            Text(
                                text = if (meetsThreshold) "$selectedDefectType | Depth: 28.4 mm" else "No subsurface fissures found",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = (if (meetsThreshold) RedHazard else EmeraldTelemetry).copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "${String.format("%.1f", detectionConfidence * 100)}%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (meetsThreshold) RedHazard else EmeraldTelemetry,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Edge Inference Performance Metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LidarMetricCard(
                title = "Edge Latency",
                value = "3.8 ms",
                unit = "NPU FP16",
                color = CyanNeon,
                modifier = Modifier.weight(1f)
            )
            LidarMetricCard(
                title = "Model Size",
                value = "12.4 MB",
                unit = "Quantized CNN",
                color = EmeraldTelemetry,
                modifier = Modifier.weight(1f)
            )
            LidarMetricCard(
                title = "Throughput",
                value = "120 FPS",
                unit = "Real-Time Train",
                color = AmberSignal,
                modifier = Modifier.weight(1f)
            )
        }

        // Defect Simulation Controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TechNavy800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DETECTION THRESHOLD & ANOMALY CLASS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CyanNeon
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "YOLO Confidence Threshold (IoU)", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Text(
                        text = "${(confidenceThreshold * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = CyanNeon
                    )
                }
                Slider(
                    value = confidenceThreshold,
                    onValueChange = { confidenceThreshold = it },
                    valueRange = 0.50f..0.99f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanNeon,
                        activeTrackColor = CyanNeon,
                        inactiveTrackColor = TechNavy600
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Acoustic Flaw Morphology:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Transverse Fissure", "Weld Porosity", "Bolt Hole Crack").forEach { defect ->
                        FilterChip(
                            selected = selectedDefectType == defect,
                            onClick = { selectedDefectType = defect },
                            label = { Text(defect, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedHazard,
                                selectedLabelColor = Color.White,
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
