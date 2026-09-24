package com.example.ui.connect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AppDatabase
import com.example.data.db.RecruiterNoteEntity
import com.example.model.CareerRepository
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BlueLaser
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.TechNavy600
import com.example.ui.theme.TechNavy700
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConnectScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val notesFlow = remember { db.recruiterNoteDao().getAllNotes() }
    val notes by notesFlow.collectAsState(initial = emptyList())

    var noteTopic by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var selectedPillar by remember { mutableStateOf("Edge AI Inference") }
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TechNavy900)
            .testTag("connect_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanNeon.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TechNavy800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Engineering Lead / Recruiter Hub",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Why Ryan Daly stands out in Edge AI & Systems",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyanNeon
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val highlights = listOf(
                        "Rare Dual Depth: Combines rigorous computational chemical physics (UCSD M.S.) with bare-metal C++20 and embedded systems.",
                        "Proven Edge Deployment: Deploys custom CNNs directly on edge NPUs and microcontrollers with deterministic microsecond latency.",
                        "Zero-Copy Systems: Designs lock-free ring buffers, custom RTOS dispatchers (ChannelSON), and HIL/CHIL telemetry testbeds."
                    )
                    highlights.forEach { h ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "✓", color = EmeraldTelemetry, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = h,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Direct Contact & Social Links
        item {
            Text(
                text = "DIRECT CONTACT & REPOSITORIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CyanNeon
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TechNavy800)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Email
                    ContactActionRow(
                        icon = Icons.Default.Email,
                        label = "Email Ryan Daly",
                        detail = CareerRepository.profile["email"] ?: "",
                        actionColor = CyanNeon,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${CareerRepository.profile["email"]}")
                                putExtra(Intent.EXTRA_SUBJECT, "Engineering Inquiry: Edge AI Systems Role")
                            }
                            safeLaunchIntent(context, intent)
                        }
                    )

                    // Phone
                    ContactActionRow(
                        icon = Icons.Default.Call,
                        label = "Phone / Mobile",
                        detail = CareerRepository.profile["phone"] ?: "",
                        actionColor = EmeraldTelemetry,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${CareerRepository.profile["phone"]}")
                            }
                            safeLaunchIntent(context, intent)
                        }
                    )

                    // GitHub 1
                    ContactActionRow(
                        icon = Icons.Default.OpenInBrowser,
                        label = "GitHub • mathemaphysics",
                        detail = "github.com/mathemaphysics",
                        actionColor = AmberSignal,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://${CareerRepository.profile["github1"]}"))
                            safeLaunchIntent(context, intent)
                        }
                    )

                    // GitHub 2
                    ContactActionRow(
                        icon = Icons.Default.OpenInBrowser,
                        label = "GitHub • snarfable",
                        detail = "github.com/snarfable",
                        actionColor = BlueLaser,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://${CareerRepository.profile["github2"]}"))
                            safeLaunchIntent(context, intent)
                        }
                    )
                }
            }
        }

        // Recruiter Interview Notes (Room Database Persistence)
        item {
            Text(
                text = "INTERVIEW NOTES & EVALUATION LOG",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CyanNeon
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = TechNavy800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Add Private Candidate Evaluation Note",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Notes are saved locally on device via Room SQLite database.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = noteTopic,
                        onValueChange = { noteTopic = it },
                        label = { Text("Topic / Question Title") },
                        placeholder = { Text("e.g. C++20 Ray-Tracing Discussion") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recruiter_topic_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = TechNavy600,
                            focusedLabelColor = CyanNeon
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Evaluation Feedback / Interview Notes") },
                        placeholder = { Text("Candidate demonstrated deep mastery of ultrasonic A-Scan spectrograms...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("recruiter_note_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = TechNavy600,
                            focusedLabelColor = CyanNeon
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Select Strength Tag
                    Text(
                        text = "Candidate Competency Category:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Edge AI Inference", "LiDAR Ray-Tracing", "C++20 Systems", "HIL / CHIL").forEach { tag ->
                            FilterChip(
                                selected = selectedPillar == tag,
                                onClick = { selectedPillar = tag },
                                label = { Text(tag, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanNeon,
                                    selectedLabelColor = TechNavy900,
                                    containerColor = TechNavy700,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (noteTopic.isNotBlank() && noteContent.isNotBlank()) {
                                scope.launch(Dispatchers.IO) {
                                    db.recruiterNoteDao().insertNote(
                                        RecruiterNoteEntity(
                                            topic = noteTopic.trim(),
                                            candidateStrength = selectedPillar,
                                            note = noteContent.trim()
                                        )
                                    )
                                    withContext(Dispatchers.Main) {
                                        noteTopic = ""
                                        noteContent = ""
                                        Toast.makeText(context, "Evaluation note saved!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please enter both title and notes.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_recruiter_note_button")
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = TechNavy900)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save Note to Local Database", color = TechNavy900, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Saved Notes List
        if (notes.isNotEmpty()) {
            items(notes, key = { it.id }) { savedNote ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, TechNavy600, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = TechNavy700)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = savedNote.topic,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )

                            FilledIconButton(
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        db.recruiterNoteDao().deleteById(savedNote.id)
                                    }
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Transparent),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Note", tint = Color.Red.copy(alpha = 0.7f))
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CyanNeon.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = savedNote.candidateStrength,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = CyanNeon,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = savedNote.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                        Text(
                            text = sdf.format(Date(savedNote.timestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Technical FAQ & Interview Talking Points
        item {
            Text(
                text = "TECHNICAL INTERVIEW FAQ & TALKING POINTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CyanNeon
            )
        }

        val faqs = listOf(
            Pair(
                "Why adapt YOLO CNNs to ultrasonic sound waves instead of RGB cameras?",
                "Railway steel internal fissures (such as transverse defects) occur sub-surface and cannot be observed visually by cameras. By transforming time-of-flight acoustic A-Scan pulses into 2D time-frequency spectrograms, we can leverage convolutional receptive fields to detect anomaly echo clusters with 98.4% accuracy at 120 FPS directly on the train."
            ),
            Pair(
                "How does multi-threaded C++20 ray-tracing benchmark perception before field trials?",
                "Physical field trials in rail and defense are dangerous and cost hundreds of thousands of dollars. Ryan's C++ optical ray-tracing simulator models beam divergence, surface roughness reflectance (Lambertian/specular), and occlusion to generate synthetic point clouds that stress-test perception models before deployment."
            ),
            Pair(
                "What was the architecture behind Elcotec's ChannelSON embedded OS?",
                "Microcontrollers have severely restricted RAM (e.g. 32 KB). Dynamic heap memory allocations (malloc/free) risk fatal heap fragmentation and unbounded execution latency. ChannelSON parses JSON-like serial configuration structures in-place directly from SPI flash sector buffers, eliminating dynamic memory allocations entirely."
            ),
            Pair(
                "How does an M.S. in Computational Chemical Physics inform Edge AI?",
                "Non-linear molecular dynamics and scientific HPC require solving differential equations, managing multi-terabyte spatial arrays, and understanding physical transport phenomena. This mathematical rigor prevents treating neural networks as black boxes and informs physics-guided neural architectures."
            )
        )

        items(faqs.indices.toList()) { index ->
            val (q, a) = faqs[index]
            val isExpanded = expandedFaqIndex == index

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, TechNavy600, RoundedCornerShape(12.dp))
                    .clickable { expandedFaqIndex = if (isExpanded) null else index },
                colors = CardDefaults.cardColors(containerColor = TechNavy800)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = q,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CyanNeon
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = a,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ContactActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    detail: String,
    actionColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = TechNavy700,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(actionColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = actionColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = label, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text(text = detail, style = MaterialTheme.typography.bodySmall, color = actionColor)
                }
            }
            Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = null,
                tint = actionColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun safeLaunchIntent(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "No compatible application found", Toast.LENGTH_SHORT).show()
    }
}
