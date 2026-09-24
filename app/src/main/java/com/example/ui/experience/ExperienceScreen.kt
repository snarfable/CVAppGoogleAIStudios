package com.example.ui.experience

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CareerRepository
import com.example.model.ExperienceItem
import com.example.model.ProjectItem
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.TechNavy600
import com.example.ui.theme.TechNavy700
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900
import com.example.ui.theme.VioletPulse

@Composable
fun ExperienceScreen(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf("All") }
    var expandedCodeSnippetId by remember { mutableStateOf<String?>("northrop") }

    val filteredExperiences = when (selectedFilter) {
        "Defense & Space" -> CareerRepository.experiences.filter { it.id in listOf("northrop", "raytheon") }
        "Vision & ML" -> CareerRepository.experiences.filter { it.id in listOf("herzog") }
        "Embedded & HPC" -> CareerRepository.experiences.filter { it.id in listOf("elcotec", "uiowa") }
        else -> CareerRepository.experiences
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TechNavy900)
            .testTag("experience_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Profile Summary Card
        item {
            ProfileSummaryHeader()
        }

        // Filter Categories
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    val filters = listOf("All", "Defense & Space", "Vision & ML", "Embedded & HPC")
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanNeon,
                                selectedLabelColor = TechNavy900,
                                containerColor = TechNavy800,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Resume Skills & Project Categories Analytics Visualization
        item {
            com.example.ui.skills.SkillsVisualizationDashboard()
        }

        // Section Title: Professional Experience
        item {
            Text(
                text = "PROFESSIONAL CHRONOLOGY (${filteredExperiences.size})",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CyanNeon
            )
        }

        // Experience Timeline Cards
        items(filteredExperiences, key = { it.id }) { item ->
            val isCodeExpanded = expandedCodeSnippetId == item.id
            ExperienceCard(
                item = item,
                isCodeExpanded = isCodeExpanded,
                onToggleCode = {
                    expandedCodeSnippetId = if (isCodeExpanded) null else item.id
                }
            )
        }

        // Section Title: Key AI/ML Systems Projects
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "FEATURED SYSTEMS & AI PROJECTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CyanNeon
            )
        }

        items(CareerRepository.keyProjects, key = { it.id }) { project ->
            ProjectCard(project = project)
        }

        // Education & Academic Background
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "EDUCATION & SCIENTIFIC FOUNDATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CyanNeon
            )
        }

        items(CareerRepository.education) { edu ->
            EducationCard(
                degree = edu["degree"] ?: "",
                school = edu["school"] ?: "",
                highlights = edu["highlights"] ?: ""
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileSummaryHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyanNeon.copy(alpha = 0.35f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = CareerRepository.profile["name"] ?: "",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = CareerRepository.profile["headline"] ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = CyanNeon
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldTelemetry.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldTelemetry)
                ) {
                    Text(
                        text = "ACTIVE ENGINEER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = EmeraldTelemetry,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TechNavy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = CareerRepository.profile["pillars"] ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(lineHeight = 16.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = CareerRepository.profile["summary"] ?: "",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ExperienceCard(
    item: ExperienceItem,
    isCodeExpanded: Boolean,
    onToggleCode: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TechNavy600, RoundedCornerShape(16.dp))
            .testTag("experience_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Company, Role, Period
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.role,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${item.company} • ${item.location}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = CyanNeon
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TechNavy900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechNavy600)
                ) {
                    Text(
                        text = item.period,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = AmberSignal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bullet Points
            item.bulletPoints.forEach { point ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "▹",
                        color = CyanNeon,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Architecture Note Callout
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TechNavy900,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ARCHITECTURE & HARDWARE DESIGN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = CyanNeon
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.architectureNote,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.tags.take(4).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TechNavy700
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Code Snippet Accordion Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TechNavy700)
                    .clickable { onToggleCode() }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.codeSnippetTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Icon(
                    imageVector = if (isCodeExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CyanNeon
                )
            }

            // Expandable Code Block
            AnimatedVisibility(visible = isCodeExpanded) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF070B14),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechNavy600),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = item.codeSnippet,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        ),
                        color = CyanNeon,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: ProjectItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = project.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = CyanNeon
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                project.metrics.forEach { (label, value) ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(6.dp),
                        color = TechNavy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, TechNavy700)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = EmeraldTelemetry
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = TechNavy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Key Innovation: ${project.keyInnovation}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = AmberSignal,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun EducationCard(degree: String, school: String, highlights: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TechNavy600, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(VioletPulse.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = VioletPulse,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = degree,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = school,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = CyanNeon
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = highlights,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
