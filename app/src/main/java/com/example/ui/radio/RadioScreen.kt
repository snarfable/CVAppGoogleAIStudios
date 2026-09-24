package com.example.ui.radio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.RadioShowRepository
import com.example.model.TranscriptLine
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.BlueLaser
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.TechNavy600
import com.example.ui.theme.TechNavy700
import com.example.ui.theme.TechNavy800
import com.example.ui.theme.TechNavy900
import com.example.ui.theme.VioletPulse

@Composable
fun RadioScreen(
    playerManager: RadioPlayerManager,
    modifier: Modifier = Modifier
) {
    val state by playerManager.state.collectAsState()
    val listState = rememberLazyListState()
    var showShowNotes by remember { mutableStateOf(false) }

    // Auto-scroll transcript to active line during playback
    LaunchedEffect(state.currentLineIndex) {
        if (state.isPlaying && state.currentLineIndex in RadioShowRepository.transcriptLines.indices) {
            // Scroll smoothly so active line is near top
            listState.animateScrollToItem(state.currentLineIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(TechNavy900)
            .testTag("radio_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Episode Header Card
        item {
            EpisodeHeroCard(
                state = state,
                onToggleNotes = { showShowNotes = !showShowNotes },
                showNotes = showShowNotes
            )
        }

        // Show Notes Expandable
        item {
            AnimatedVisibility(visible = showShowNotes) {
                ShowNotesCard()
            }
        }

        // Audio Player Controls & Waveform Visualizer
        item {
            AudioPlayerControlsCard(
                state = state,
                onPlayPause = { playerManager.togglePlayPause() },
                onSeek = { seconds -> playerManager.seekToSeconds(seconds) },
                onSkipBack = { playerManager.skipBackward10() },
                onSkipForward = { playerManager.skipForward10() },
                onSpeedChange = { speed -> playerManager.setPlaybackSpeed(speed) }
            )
        }

        // Active Roundtable Speakers Lineup
        item {
            SpeakersLineupRow(activeSpeakerId = state.activeSpeakerId)
        }

        // Transcript Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE TIMECODED TRANSCRIPT",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CyanNeon
                )
                Text(
                    text = "Tap line to jump",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Transcript Lines
        items(RadioShowRepository.transcriptLines, key = { it.index }) { line ->
            val isActive = state.currentLineIndex == line.index
            TranscriptLineCard(
                line = line,
                isActive = isActive,
                isPlaying = state.isPlaying && isActive,
                onClick = { playerManager.jumpToLine(line.index) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EpisodeHeroCard(
    state: RadioPlayerState,
    onToggleNotes: () -> Unit,
    showNotes: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, TechNavy600, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_edge_studio_1790220707121),
                    contentDescription = "Radio Studio Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    TechNavy800.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Episode Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (state.isPlaying) EmeraldTelemetry else AmberSignal)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isPlaying) "ON AIR • LIVE" else "ROUNDTABLE EPISODE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                // Duration badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = RadioShowRepository.TOTAL_DURATION_FORMATTED,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = CyanNeon
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = RadioShowRepository.EPISODE_TITLE,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = RadioShowRepository.EPISODE_SUBTITLE,
                    style = MaterialTheme.typography.bodySmall,
                    color = CyanNeon
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = RadioShowRepository.EPISODE_SUMMARY,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = EmeraldTelemetry,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isHighQualityVoiceMode) "Neural Voice Mode Active" else "Multi-Speaker Synthesizer Active",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = EmeraldTelemetry
                        )
                    }

                    Text(
                        text = if (showNotes) "Hide Show Notes" else "View Show Notes",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyanNeon,
                        modifier = Modifier
                            .clickable { onToggleNotes() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ShowNotesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TechNavy600, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy700)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Episode Show Notes & Technical Pillars",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            val notes = listOf(
                "First-Principles Physics Foundation: M.S. Computational Chemical Physics (UCSD) & B.S. Chemical Engineering (Iowa) - molecular dynamics, statistical mechanics, and Fortran/C++.",
                "Ultrasonic YOLO Discovery: Adapted custom YOLO CNN to acoustic waveforms for real-time rail flaw detection at Herzog Technologies.",
                "Multi-Threaded LiDAR Ray-Tracing: Authored C++20 optical physics simulator replicating beam divergence, surface reflectance, and occlusion.",
                "Hardware-in-the-Loop (HIL/CHIL): Feeding high-throughput synthetic telemetry to edge guidance hardware deterministically at Raytheon.",
                "Embedded Operating System: Designed ChannelSON lightweight event dispatcher parsing serial config in flash memory at Elcotec.",
                "Mission-Critical Edge Compute: Northrop Grumman low-latency event APIs and deterministic inter-board telemetry."
            )
            notes.forEach { note ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "•", color = CyanNeon, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AudioPlayerControlsCard(
    state: RadioPlayerState,
    onPlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSkipBack: () -> Unit,
    onSkipForward: () -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = TechNavy800)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Audio Equalizer Waveform Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TechNavy900)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.waveformAmplitudes.forEachIndexed { idx, amp ->
                    val animatedHeight by animateDpAsState(
                        targetValue = if (state.isPlaying) (amp * 36).coerceIn(4f, 36f).dp else 4.dp,
                        animationSpec = tween(80, easing = FastOutSlowInEasing),
                        label = "bar_$idx"
                    )
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(animatedHeight)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CyanNeon,
                                        if (idx % 2 == 0) BlueLaser else EmeraldTelemetry
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time scrub slider
            Slider(
                value = state.currentPositionSeconds.toFloat(),
                onValueChange = { onSeek(it.toInt()) },
                valueRange = 0f..RadioShowRepository.TOTAL_DURATION_SECONDS.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("audio_scrubber_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = CyanNeon,
                    activeTrackColor = CyanNeon,
                    inactiveTrackColor = TechNavy600
                )
            )

            // Current Time / Duration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val currentM = state.currentPositionSeconds / 60
                val currentS = state.currentPositionSeconds % 60
                Text(
                    text = String.format("%02d:%02d", currentM, currentS),
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyanNeon
                )
                Text(
                    text = RadioShowRepository.TOTAL_DURATION_FORMATTED,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Playback buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback speed toggle
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TechNavy700,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechNavy600),
                    modifier = Modifier.clickable {
                        val nextSpeed = when (state.playbackSpeed) {
                            1.0f -> 1.25f
                            1.25f -> 1.5f
                            else -> 1.0f
                        }
                        onSpeedChange(nextSpeed)
                    }
                ) {
                    Text(
                        text = "${state.playbackSpeed}x",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = CyanNeon,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Rewind 10s
                FilledIconButton(
                    onClick = onSkipBack,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = TechNavy700),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind 10 seconds",
                        tint = Color.White
                    )
                }

                // Master Play/Pause Button
                FilledIconButton(
                    onClick = onPlayPause,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = CyanNeon),
                    modifier = Modifier
                        .size(64.dp)
                        .testTag("radio_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause Radio" else "Play Radio",
                        tint = TechNavy900,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Forward 10s
                FilledIconButton(
                    onClick = onSkipForward,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = TechNavy700),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 10 seconds",
                        tint = Color.White
                    )
                }

                // Speaker on-air tag
                val activeSpeaker = RadioShowRepository.speakers[state.activeSpeakerId]
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = (activeSpeaker?.avatarColor ?: CyanNeon).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        activeSpeaker?.avatarColor ?: CyanNeon
                    )
                ) {
                    Text(
                        text = activeSpeaker?.name ?: "Host",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = activeSpeaker?.avatarColor ?: CyanNeon,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SpeakersLineupRow(activeSpeakerId: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "ROUNDTABLE PARTICIPANTS",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(RadioShowRepository.speakers.values.toList(), key = { it.id }) { speaker ->
                val isSpeaking = activeSpeakerId == speaker.id
                val borderColor by animateColorAsState(
                    targetValue = if (isSpeaking) speaker.avatarColor else TechNavy600,
                    animationSpec = tween(200),
                    label = "border_${speaker.id}"
                )

                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .border(if (isSpeaking) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSpeaking) TechNavy700 else TechNavy800
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(speaker.avatarColor.copy(alpha = 0.25f))
                                .border(1.5.dp, speaker.avatarColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = speaker.name.take(1),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = speaker.avatarColor
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = speaker.name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = speaker.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanNeon
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isSpeaking) "● SPEAKING" else speaker.role.take(15) + "...",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isSpeaking) EmeraldTelemetry else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TranscriptLineCard(
    line: TranscriptLine,
    isActive: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val speaker = RadioShowRepository.speakers[line.speakerId] ?: return
    val cardBackground by animateColorAsState(
        targetValue = if (isActive) TechNavy700 else TechNavy800,
        animationSpec = tween(200),
        label = "bg_${line.index}"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isActive) speaker.avatarColor else TechNavy600.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "border_${line.index}"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(if (isActive) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("transcript_line_${line.index}"),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(speaker.avatarColor.copy(alpha = 0.2f))
                            .border(1.dp, speaker.avatarColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = speaker.name.take(1),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = speaker.avatarColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = speaker.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${speaker.location})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isActive) speaker.avatarColor.copy(alpha = 0.2f) else TechNavy900,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (isActive) speaker.avatarColor else TechNavy600
                    )
                ) {
                    Text(
                        text = line.formattedTimecode,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isActive) speaker.avatarColor else CyanNeon,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = line.dialogue,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
                ),
                color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TechNavy900.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = line.technicalTheme,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (isActive) {
                    Text(
                        text = if (isPlaying) "● NOW PLAYING" else "PAUSED HERE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isPlaying) EmeraldTelemetry else AmberSignal
                    )
                }
            }
        }
    }
}
