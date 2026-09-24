package com.example.ui.skills

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CareerRepository
import com.example.model.ProjectCategoryData
import com.example.model.SkillMetric
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BlueLaser
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.RedHazard
import com.example.ui.theme.TechNavy500
import com.example.ui.theme.TechNavy600
import com.example.ui.theme.TechNavy700
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900
import com.example.ui.theme.VioletPulse
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class ChartType(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    RADAR("Radar Chart", Icons.Default.ShowChart),
    BAR("Bar Chart", Icons.Default.BarChart),
    DONUT("Donut Chart", Icons.Default.PieChart)
}

/**
 * Interactive D3/Recharts-inspired technical skills and project categories data visualization dashboard.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillsVisualizationDashboard(
    modifier: Modifier = Modifier
) {
    var selectedChart by remember { mutableStateOf(ChartType.RADAR) }
    var selectedCategoryIndex by remember { mutableStateOf<Int?>(null) }
    var selectedSkillIndex by remember { mutableStateOf<Int?>(null) }

    val categories = CareerRepository.projectCategories
    val topSkills = CareerRepository.topSkillsMetrics

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CyanNeon.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .testTag("skills_visualization_dashboard"),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Dashboard Header with D3/Recharts badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SKILL & DOMAIN ANALYTICS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = CyanNeon
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyanNeon.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "D3 / RECHARTS VISUALIZER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = CyanNeon
                            )
                        }
                    }
                    Text(
                        text = "Resume Analysis & Project Weighting",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Mode Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChartType.values().forEach { chart ->
                    FilterChip(
                        selected = selectedChart == chart,
                        onClick = {
                            selectedChart = chart
                            selectedCategoryIndex = null
                            selectedSkillIndex = null
                        },
                        label = { Text(chart.label, style = MaterialTheme.typography.labelSmall) },
                        leadingIcon = {
                            Icon(
                                imageVector = chart.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (selectedChart == chart) TechNavy900 else CyanNeon
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanNeon,
                            selectedLabelColor = TechNavy900,
                            containerColor = TechNavy700,
                            labelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Render Canvas Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TechNavy900)
                    .border(1.dp, TechNavy600, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                when (selectedChart) {
                    ChartType.RADAR -> {
                        SkillsRadarChart(
                            categories = categories,
                            selectedIndex = selectedCategoryIndex,
                            onSelectIndex = { selectedCategoryIndex = it }
                        )
                    }
                    ChartType.BAR -> {
                        SkillsBarChart(
                            skills = topSkills,
                            selectedIndex = selectedSkillIndex,
                            onSelectIndex = { selectedSkillIndex = it }
                        )
                    }
                    ChartType.DONUT -> {
                        ProjectCategoriesDonutChart(
                            categories = categories,
                            selectedIndex = selectedCategoryIndex,
                            onSelectIndex = { selectedCategoryIndex = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Tooltip / Detail Card
            if (selectedChart == ChartType.BAR && selectedSkillIndex != null && selectedSkillIndex!! in topSkills.indices) {
                val skill = topSkills[selectedSkillIndex!!]
                DetailCard(
                    title = skill.name,
                    category = skill.category,
                    metricLabel = "Proficiency",
                    metricValue = "${skill.score}%",
                    extraInfo = "Years Exp: ${skill.yearsExp} yrs • Top Focus: ${skill.highlight}",
                    accentColor = skill.accentColor
                )
            } else if (selectedCategoryIndex != null && selectedCategoryIndex!! in categories.indices) {
                val cat = categories[selectedCategoryIndex!!]
                DetailCard(
                    title = cat.name,
                    category = "${cat.projectCount} Core Projects • ${cat.experiencePercentage}% of Total Career Portfolio",
                    metricLabel = "Score",
                    metricValue = "${cat.proficiencyScore}/100",
                    extraInfo = "Key Stack: " + cat.keyTech.joinToString(", "),
                    accentColor = cat.color
                )
            } else {
                // Default legend / guidance note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TechNavy700.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tap on any ${if (selectedChart == ChartType.BAR) "bar" else if (selectedChart == ChartType.RADAR) "vertex" else "arc slice"} to inspect metrics, project counts, and tech stacks.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Legend Badges
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEachIndexed { index, cat ->
                    val isSelected = selectedCategoryIndex == index
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) cat.color.copy(alpha = 0.25f) else TechNavy700)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) cat.color else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                selectedCategoryIndex = if (isSelected) null else index
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(cat.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cat.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${cat.proficiencyScore}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = cat.color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    category: String,
    metricLabel: String,
    metricValue: String,
    extraInfo: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy700)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanNeon
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = extraInfo,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = metricLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = metricValue,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = accentColor
                )
            }
        }
    }
}

/**
 * D3/Recharts-style multi-axis Radar Chart with concentric grid webs and animated polygon fills.
 */
@Composable
fun SkillsRadarChart(
    categories: List<ProjectCategoryData>,
    selectedIndex: Int?,
    onSelectIndex: (Int?) -> Unit
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(700))
    }

    val vertexCount = categories.size
    if (vertexCount < 3) return

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(categories) {
                detectTapGestures { offset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val dist = sqrt(dx * dx + dy * dy)
                    val maxRadius = minOf(centerX, centerY) * 0.72f

                    if (dist <= maxRadius * 1.3f) {
                        var angle = atan2(dy, dx) * 180f / PI.toFloat()
                        // Offset by -90 deg since radar starts at top (-PI/2)
                        angle = (angle + 90f + 360f) % 360f
                        val sliceAngle = 360f / vertexCount
                        val clickedIndex = ((angle + sliceAngle / 2f) / sliceAngle).toInt() % vertexCount
                        onSelectIndex(if (selectedIndex == clickedIndex) null else clickedIndex)
                    } else {
                        onSelectIndex(null)
                    }
                }
            }
            .testTag("radar_chart_canvas")
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = minOf(centerX, centerY) * 0.72f
        val angleStep = (2 * PI / vertexCount).toFloat()

        // 1. Draw Concentric Polygonal Web Grid (25%, 50%, 75%, 100%)
        val gridLevels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
        gridLevels.forEach { level ->
            val levelRadius = radius * level
            val webPath = Path()
            for (i in 0 until vertexCount) {
                val angle = i * angleStep - PI.toFloat() / 2f
                val x = centerX + levelRadius * cos(angle)
                val y = centerY + levelRadius * sin(angle)
                if (i == 0) webPath.moveTo(x, y) else webPath.lineTo(x, y)
            }
            webPath.close()

            drawPath(
                path = webPath,
                color = TechNavy600.copy(alpha = if (level == 1f) 0.8f else 0.4f),
                style = Stroke(width = if (level == 1f) 1.5f else 1f)
            )

            // Draw level labels (e.g., 50%, 100%) on vertical axis
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(140, 148, 163, 184)
                    textSize = 9.dp.toPx()
                    isAntiAlias = true
                }
                drawText(
                    "${(level * 100).toInt()}%",
                    centerX + 4f,
                    centerY - levelRadius + 12f,
                    paint
                )
            }
        }

        // 2. Draw Spokes / Radials
        for (i in 0 until vertexCount) {
            val angle = i * angleStep - PI.toFloat() / 2f
            val endX = centerX + radius * cos(angle)
            val endY = centerY + radius * sin(angle)
            drawLine(
                color = TechNavy600.copy(alpha = 0.5f),
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = 1f
            )

            // Draw Category Labels around outer perimeter
            val labelDistance = radius * 1.22f
            val labelX = centerX + labelDistance * cos(angle)
            val labelY = centerY + labelDistance * sin(angle)

            val cat = categories[i]
            val isSelected = selectedIndex == i

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = if (isSelected) cat.color.toArgb() else android.graphics.Color.WHITE
                    textSize = if (isSelected) 11.dp.toPx() else 10.dp.toPx()
                    isFakeBoldText = isSelected
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                // Short label
                val label = cat.name.split(" ").take(2).joinToString(" ")
                drawText(label, labelX, labelY + 4f, paint)
            }
        }

        // 3. Draw Data Polygon Area (Animated)
        val dataPath = Path()
        val points = mutableListOf<Offset>()

        for (i in 0 until vertexCount) {
            val cat = categories[i]
            val normalizedScore = (cat.proficiencyScore / 100f) * animProgress.value
            val pointRadius = radius * normalizedScore
            val angle = i * angleStep - PI.toFloat() / 2f
            val px = centerX + pointRadius * cos(angle)
            val py = centerY + pointRadius * sin(angle)
            points.add(Offset(px, py))
            if (i == 0) dataPath.moveTo(px, py) else dataPath.lineTo(px, py)
        }
        dataPath.close()

        // Fill radar shape with Cyan to Blue gradient glow
        drawPath(
            path = dataPath,
            brush = Brush.radialGradient(
                colors = listOf(CyanNeon.copy(alpha = 0.45f), BlueLaser.copy(alpha = 0.15f)),
                center = Offset(centerX, centerY),
                radius = radius
            )
        )

        // Draw radar outline stroke
        drawPath(
            path = dataPath,
            color = CyanNeon,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // 4. Draw Vertex Nodes
        points.forEachIndexed { i, pt ->
            val cat = categories[i]
            val isSelected = selectedIndex == i

            // Glow ring if selected
            if (isSelected) {
                drawCircle(
                    color = cat.color.copy(alpha = 0.35f),
                    radius = 12f,
                    center = pt
                )
            }

            drawCircle(
                color = if (isSelected) Color.White else cat.color,
                radius = if (isSelected) 6f else 4.5f,
                center = pt
            )
            drawCircle(
                color = TechNavy900,
                radius = 2.5f,
                center = pt,
                style = Stroke(width = 1.5f)
            )
        }
    }
}

/**
 * D3/Recharts-style Horizontal Bar Chart with gradient fills, gridlines, and animated widths.
 */
@Composable
fun SkillsBarChart(
    skills: List<SkillMetric>,
    selectedIndex: Int?,
    onSelectIndex: (Int?) -> Unit
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(650))
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(skills) {
                detectTapGestures { offset ->
                    val topPadding = 20f
                    val bottomPadding = 30f
                    val availableHeight = size.height - topPadding - bottomPadding
                    val barSpacing = availableHeight / skills.size

                    val relativeY = offset.y - topPadding
                    if (relativeY >= 0 && relativeY <= availableHeight) {
                        val index = (relativeY / barSpacing).toInt()
                        if (index in skills.indices) {
                            onSelectIndex(if (selectedIndex == index) null else index)
                        }
                    } else {
                        onSelectIndex(null)
                    }
                }
            }
            .testTag("bar_chart_canvas")
    ) {
        val labelWidth = 140.dp.toPx()
        val rightPadding = 45.dp.toPx()
        val topPadding = 16.dp.toPx()
        val bottomPadding = 26.dp.toPx()

        val chartWidth = size.width - labelWidth - rightPadding
        val chartHeight = size.height - topPadding - bottomPadding
        val barCount = skills.size
        val rowHeight = chartHeight / barCount
        val barHeight = rowHeight * 0.58f

        // 1. Draw Vertical Grid Lines (0%, 25%, 50%, 75%, 100%)
        val gridSteps = listOf(0f, 0.25f, 0.5f, 0.75f, 1.0f)
        gridSteps.forEach { step ->
            val x = labelWidth + chartWidth * step
            drawLine(
                color = TechNavy600.copy(alpha = 0.45f),
                start = Offset(x, topPadding),
                end = Offset(x, topPadding + chartHeight),
                strokeWidth = 1f
            )

            // X-axis scale labels
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(160, 148, 163, 184)
                    textSize = 9.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                drawText("${(step * 100).toInt()}%", x, topPadding + chartHeight + 16.dp.toPx(), paint)
            }
        }

        // 2. Draw Bars and Y-Axis Labels
        skills.forEachIndexed { i, skill ->
            val y = topPadding + i * rowHeight + (rowHeight - barHeight) / 2f
            val isSelected = selectedIndex == i
            val currentWidth = chartWidth * (skill.score / 100f) * animProgress.value

            // Y-Axis Skill Label
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = if (isSelected) skill.accentColor.toArgb() else android.graphics.Color.WHITE
                    textSize = 10.dp.toPx()
                    isFakeBoldText = isSelected
                    textAlign = android.graphics.Paint.Align.RIGHT
                    isAntiAlias = true
                }
                val truncatedName = if (skill.name.length > 18) skill.name.take(16) + "…" else skill.name
                drawText(truncatedName, labelWidth - 10f, y + barHeight * 0.75f, paint)
            }

            // Bar Background Track
            drawRoundRect(
                color = TechNavy700,
                topLeft = Offset(labelWidth, y),
                size = Size(chartWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Filled Bar with Gradient
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        skill.accentColor.copy(alpha = 0.7f),
                        if (isSelected) Color.White else skill.accentColor
                    ),
                    startX = labelWidth,
                    endX = labelWidth + currentWidth
                ),
                topLeft = Offset(labelWidth, y),
                size = Size(currentWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Outer highlight if selected
            if (isSelected) {
                drawRoundRect(
                    color = skill.accentColor,
                    topLeft = Offset(labelWidth, y),
                    size = Size(currentWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Value text at end of bar
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = if (isSelected) skill.accentColor.toArgb() else android.graphics.Color.argb(220, 248, 250, 252)
                    textSize = 10.dp.toPx()
                    isFakeBoldText = true
                    textAlign = android.graphics.Paint.Align.LEFT
                    isAntiAlias = true
                }
                drawText("${skill.score}%", labelWidth + currentWidth + 6.dp.toPx(), y + barHeight * 0.75f, paint)
            }
        }
    }
}

/**
 * D3/Recharts-style Donut Chart showing Project & Experience Category allocation percentages.
 */
@Composable
fun ProjectCategoriesDonutChart(
    categories: List<ProjectCategoryData>,
    selectedIndex: Int?,
    onSelectIndex: (Int?) -> Unit
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(700))
    }

    val totalShare = categories.sumOf { it.experiencePercentage }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(categories) {
                detectTapGestures { offset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val dist = sqrt(dx * dx + dy * dy)
                    val outerRadius = minOf(centerX, centerY) * 0.85f
                    val innerRadius = outerRadius * 0.54f

                    if (dist in (innerRadius * 0.8f)..(outerRadius * 1.15f)) {
                        var angle = atan2(dy, dx) * 180f / PI.toFloat()
                        angle = (angle + 360f) % 360f // normalize to 0..360

                        var accumAngle = 0f
                        var hitIndex: Int? = null
                        for (i in categories.indices) {
                            val sweep = (categories[i].experiencePercentage.toFloat() / totalShare) * 360f
                            if (angle >= accumAngle && angle <= accumAngle + sweep) {
                                hitIndex = i
                                break
                            }
                            accumAngle += sweep
                        }
                        if (hitIndex != null) {
                            onSelectIndex(if (selectedIndex == hitIndex) null else hitIndex)
                        }
                    } else {
                        onSelectIndex(null)
                    }
                }
            }
            .testTag("donut_chart_canvas")
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val outerRadius = minOf(centerX, centerY) * 0.82f
        val strokeWidth = outerRadius * 0.42f
        val chartRadius = outerRadius - strokeWidth / 2f

        var currentStartAngle = -90f

        categories.forEachIndexed { i, cat ->
            val sweep = (cat.experiencePercentage.toFloat() / totalShare) * 360f * animProgress.value
            val isSelected = selectedIndex == i

            // Arc stroke
            drawArc(
                color = if (isSelected) cat.color else cat.color.copy(alpha = 0.85f),
                startAngle = currentStartAngle,
                sweepAngle = sweep - 1.8f, // small gap between slices
                useCenter = false,
                topLeft = Offset(centerX - chartRadius, centerY - chartRadius),
                size = Size(chartRadius * 2, chartRadius * 2),
                style = Stroke(
                    width = if (isSelected) strokeWidth * 1.15f else strokeWidth,
                    cap = StrokeCap.Butt
                )
            )

            currentStartAngle += (cat.experiencePercentage.toFloat() / totalShare) * 360f
        }

        // Center HUD in donut hole
        drawContext.canvas.nativeCanvas.apply {
            val titlePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 14.dp.toPx()
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            val subPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(180, 0, 229, 255)
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            if (selectedIndex != null && selectedIndex in categories.indices) {
                val cat = categories[selectedIndex]
                drawText("${cat.experiencePercentage}%", centerX, centerY - 2.dp.toPx(), titlePaint)
                drawText(cat.name.split(" ").first(), centerX, centerY + 14.dp.toPx(), subPaint)
            } else {
                drawText("PORTFOLIO", centerX, centerY - 2.dp.toPx(), titlePaint)
                drawText("4 DOMAINS", centerX, centerY + 14.dp.toPx(), subPaint)
            }
        }
    }
}
