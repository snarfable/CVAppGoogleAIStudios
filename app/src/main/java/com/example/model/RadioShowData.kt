package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AmberSignal
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldTelemetry
import com.example.ui.theme.VioletPulse

data class SpeakerInfo(
    val id: String,
    val name: String,
    val role: String,
    val location: String,
    val avatarColor: Color,
    val speechPitch: Float,
    val speechRate: Float
)

data class TranscriptLine(
    val index: Int,
    val startSeconds: Int,
    val durationSeconds: Int,
    val speakerId: String,
    val dialogue: String,
    val technicalTheme: String
) {
    val formattedTimecode: String
        get() {
            val m = startSeconds / 60
            val s = startSeconds % 60
            return String.format("%02d:%02d", m, s)
        }
}

object RadioShowRepository {
    val speakers = mapOf(
        "paul" to SpeakerInfo(
            id = "paul",
            name = "Paul",
            role = "Radio Host & Moderator",
            location = "Studio HQ",
            avatarColor = VioletPulse,
            speechPitch = 0.85f,
            speechRate = 0.95f
        ),
        "sarah" to SpeakerInfo(
            id = "sarah",
            name = "Sarah",
            role = "Robotics & Physics Specialist",
            location = "Boston",
            avatarColor = CyanNeon,
            speechPitch = 1.25f,
            speechRate = 1.05f
        ),
        "leo" to SpeakerInfo(
            id = "leo",
            name = "Leo",
            role = "Edge AI & Vision Architect",
            location = "San Francisco",
            avatarColor = EmeraldTelemetry,
            speechPitch = 1.12f,
            speechRate = 1.10f
        ),
        "mark" to SpeakerInfo(
            id = "mark",
            name = "Mark",
            role = "Real-Time Embedded & HIL Lead",
            location = "Austin",
            avatarColor = AmberSignal,
            speechPitch = 0.92f,
            speechRate = 1.00f
        )
    )

    val transcriptLines = listOf(
        TranscriptLine(
            index = 0,
            startSeconds = 0,
            durationSeconds = 14,
            speakerId = "paul",
            dialogue = "Today we examine the engineering profile of Ryan P. Daly, exploring how first-principles computational physics directly accelerates real-time edge AI architectures.",
            technicalTheme = "Introduction & Core Profile"
        ),
        TranscriptLine(
            index = 1,
            startSeconds = 14,
            durationSeconds = 8,
            speakerId = "paul",
            dialogue = "Joining our roundtable discussion are Mark in Austin, Sarah in Boston, and Leo in San Francisco. Welcome, everyone.",
            technicalTheme = "Roundtable Introduction"
        ),
        TranscriptLine(
            index = 2,
            startSeconds = 22,
            durationSeconds = 19,
            speakerId = "sarah",
            dialogue = "Thanks, Paul. Reviewing his Master's from UCSD in Computational Chemical Physics, he brings deep expertise in non-linear molecular dynamics, numerical algorithms, and high-performance scientific compute.",
            technicalTheme = "UCSD Chemical Physics Foundation"
        ),
        TranscriptLine(
            index = 3,
            startSeconds = 41,
            durationSeconds = 20,
            speakerId = "leo",
            dialogue = "Exactly. At Herzog Technologies, he innovatively adapted custom YOLO convolutional neural networks to ultrasonic acoustic waveforms, treating non-destructive rail flaw signatures as spatial spectrograms for real-time inference.",
            technicalTheme = "Ultrasonic YOLO Anomaly Detection"
        ),
        TranscriptLine(
            index = 4,
            startSeconds = 61,
            durationSeconds = 21,
            speakerId = "mark",
            dialogue = "And in systems software, he authored a multi-threaded C++20 LiDAR ray-tracing engine modeling beam divergence and surface reflectance to generate synthetic ground-truth point clouds prior to physical field trials.",
            technicalTheme = "C++20 LiDAR Ray-Tracing Engine"
        ),
        TranscriptLine(
            index = 5,
            startSeconds = 82,
            durationSeconds = 17,
            speakerId = "sarah",
            dialogue = "At Raytheon Missiles and Defense, he engineered hardware-in-the-loop and computer-in-the-loop simulation testbeds, clock-synchronizing high-rate synthetic sensor streams to edge guidance hardware with nanosecond precision.",
            technicalTheme = "Hardware-in-the-Loop (HIL) & Determinism"
        ),
        TranscriptLine(
            index = 6,
            startSeconds = 99,
            durationSeconds = 16,
            speakerId = "leo",
            dialogue = "Deterministic execution is central to his work. At Elcotec, he architected ChannelSON, an embedded event dispatcher and operating system parsing serial configuration directly in flash memory with zero dynamic allocations.",
            technicalTheme = "ChannelSON Embedded RTOS"
        ),
        TranscriptLine(
            index = 7,
            startSeconds = 115,
            durationSeconds = 19,
            speakerId = "mark",
            dialogue = "Now at Northrop Grumman, he architects deterministic low-latency telemetry APIs for mission-critical edge compute nodes, guaranteeing zero-copy inter-board communication with sub-50 microsecond jitter.",
            technicalTheme = "Northrop Grumman Mission-Critical Edge"
        ),
        TranscriptLine(
            index = 8,
            startSeconds = 134,
            durationSeconds = 15,
            speakerId = "sarah",
            dialogue = "It represents a rare synthesis: deep mathematical physics, modern C++20 systems programming, and high-efficiency neural network deployment directly on bare-metal silicon.",
            technicalTheme = "Bare-Metal Systems Engineering"
        ),
        TranscriptLine(
            index = 9,
            startSeconds = 149,
            durationSeconds = 13,
            speakerId = "paul",
            dialogue = "A masterclass in bridging physics simulation with embedded machine learning. Thank you all for joining this engineering breakdown of Ryan Daly.",
            technicalTheme = "Wrap-up & Engineering Synthesis"
        )
    )

    const val TOTAL_DURATION_SECONDS = 162 // 02:42
    val TOTAL_DURATION_FORMATTED = "02:42"
    const val EPISODE_TITLE = "Ryan Daly: Engineering the Edge"
    const val EPISODE_SUBTITLE = "AI Talk Radio Roundtable • Episode #42"
    const val EPISODE_SUMMARY = "An in-depth technical analysis exploring Ryan P. Daly's career from computational chemical physics at UCSD to low-latency edge AI, multi-threaded LiDAR ray-tracing, ultrasonic YOLO detection, and deterministic aerospace telemetry."
}
