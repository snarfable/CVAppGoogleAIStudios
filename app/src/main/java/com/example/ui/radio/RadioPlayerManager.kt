package com.example.ui.radio

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.model.RadioShowRepository
import com.example.model.TranscriptLine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

data class RadioPlayerState(
    val isPlaying: Boolean = false,
    val currentPositionSeconds: Int = 0,
    val currentLineIndex: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val isTtsReady: Boolean = false,
    val activeSpeakerId: String = "paul",
    val waveformAmplitudes: List<Float> = List(16) { 0.1f },
    val isHighQualityVoiceMode: Boolean = true,
    val currentVoiceName: String = "Detecting High-Quality Voices..."
)

class RadioPlayerManager(
    private val context: Context,
    private val scope: CoroutineScope
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var playbackJob: Job? = null
    private var waveformJob: Job? = null

    // Speaker-to-Voice mapping for distinct neural timbre and vocal identity
    private val speakerVoiceMap = mutableMapOf<String, Voice?>()

    private val _state = MutableStateFlow(RadioPlayerState())
    val state: StateFlow<RadioPlayerState> = _state.asStateFlow()

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            configureHighQualityVoices()
            setupProgressListener()
            _state.value = _state.value.copy(
                isTtsReady = true,
                currentVoiceName = speakerVoiceMap["paul"]?.name ?: "Studio Standard Voice"
            )
        }
    }

    /**
     * Inspects available Android TTS system voices and selects high-quality / neural voices,
     * assigning unique voices across Paul (Host), Sarah, Leo, and Mark.
     */
    private fun configureHighQualityVoices() {
        val currentTts = tts ?: return
        try {
            val availableVoices = currentTts.voices?.filter { voice ->
                voice.locale.language == Locale.ENGLISH.language && !voice.isNetworkConnectionRequired
            }?.toList() ?: emptyList()

            Log.d("RadioPlayerManager", "Available English voices count: ${availableVoices.size}")

            if (availableVoices.isNotEmpty()) {
                // High-quality priority: filter very-high or high quality if available
                val sortedVoices = availableVoices.sortedWith(
                    compareByDescending<Voice> { it.quality }
                        .thenByDescending { it.latency }
                )

                // Separate male / female identifiers if voice name metadata contains hints
                val femaleVoices = sortedVoices.filter { v ->
                    val n = v.name.lowercase()
                    n.contains("female") || n.contains("fem") || n.contains("woman") || n.contains("girl") || n.contains("#female")
                }
                val maleVoices = sortedVoices.filter { v ->
                    val n = v.name.lowercase()
                    n.contains("male") || n.contains("#male") || n.contains("man") || !femaleVoices.contains(v)
                }

                // Paul: Primary Baritone Host voice
                val paulVoice = maleVoices.firstOrNull { it.name.contains("male_1") || it.name.contains("en-us") }
                    ?: sortedVoices.getOrNull(0)
                speakerVoiceMap["paul"] = paulVoice

                // Sarah: Articulate Female Specialist voice
                val sarahVoice = femaleVoices.firstOrNull()
                    ?: sortedVoices.filter { it != paulVoice }.getOrNull(1)
                    ?: sortedVoices.getOrNull(1)
                speakerVoiceMap["sarah"] = sarahVoice

                // Leo: Tech Lead voice
                val remainingMale = maleVoices.filter { it != paulVoice }
                val leoVoice = remainingMale.firstOrNull { it.name.contains("male_2") }
                    ?: remainingMale.firstOrNull()
                    ?: sortedVoices.filter { it != paulVoice && it != sarahVoice }.getOrNull(0)
                speakerVoiceMap["leo"] = leoVoice

                // Mark: Real-Time Senior Engineer voice
                val markVoice = remainingMale.filter { it != leoVoice }.firstOrNull()
                    ?: sortedVoices.filter { it != paulVoice && it != sarahVoice && it != leoVoice }.firstOrNull()
                speakerVoiceMap["mark"] = markVoice
            }
        } catch (e: Exception) {
            Log.e("RadioPlayerManager", "Error querying TTS voices: ${e.message}")
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {}
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {}
        })
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (_state.value.isPlaying) return
        _state.value = _state.value.copy(isPlaying = true)
        startPlaybackLoop()
        startWaveformAnimation()
        speakCurrentLine()
    }

    fun pause() {
        _state.value = _state.value.copy(isPlaying = false)
        playbackJob?.cancel()
        waveformJob?.cancel()
        tts?.stop()
    }

    fun seekToSeconds(seconds: Int) {
        val bounded = seconds.coerceIn(0, RadioShowRepository.TOTAL_DURATION_SECONDS)
        val lineIndex = RadioShowRepository.transcriptLines.indexOfLast { it.startSeconds <= bounded }
            .coerceAtLeast(0)
        val speakerId = RadioShowRepository.transcriptLines[lineIndex].speakerId

        _state.value = _state.value.copy(
            currentPositionSeconds = bounded,
            currentLineIndex = lineIndex,
            activeSpeakerId = speakerId,
            currentVoiceName = speakerVoiceMap[speakerId]?.name ?: "Studio Natural Voice"
        )

        if (_state.value.isPlaying) {
            speakCurrentLine()
        }
    }

    fun jumpToLine(index: Int) {
        if (index in RadioShowRepository.transcriptLines.indices) {
            val line = RadioShowRepository.transcriptLines[index]
            seekToSeconds(line.startSeconds)
            if (!_state.value.isPlaying) {
                play()
            }
        }
    }

    fun skipForward10() {
        seekToSeconds(_state.value.currentPositionSeconds + 10)
    }

    fun skipBackward10() {
        seekToSeconds(_state.value.currentPositionSeconds - 10)
    }

    fun setPlaybackSpeed(speed: Float) {
        _state.value = _state.value.copy(playbackSpeed = speed)
        if (_state.value.isPlaying) {
            speakCurrentLine()
        }
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = scope.launch(Dispatchers.Default) {
            while (isActive && _state.value.isPlaying) {
                val delayMs = (1000 / _state.value.playbackSpeed).toLong()
                delay(delayMs)

                val newSeconds = _state.value.currentPositionSeconds + 1
                if (newSeconds >= RadioShowRepository.TOTAL_DURATION_SECONDS) {
                    // Finished show
                    _state.value = _state.value.copy(
                        isPlaying = false,
                        currentPositionSeconds = RadioShowRepository.TOTAL_DURATION_SECONDS
                    )
                    break
                }

                // Check if current line changed
                val newLineIndex = RadioShowRepository.transcriptLines.indexOfLast { it.startSeconds <= newSeconds }
                    .coerceAtLeast(0)

                val lineChanged = newLineIndex != _state.value.currentLineIndex
                val activeSpeaker = RadioShowRepository.transcriptLines[newLineIndex].speakerId

                _state.value = _state.value.copy(
                    currentPositionSeconds = newSeconds,
                    currentLineIndex = newLineIndex,
                    activeSpeakerId = activeSpeaker,
                    currentVoiceName = speakerVoiceMap[activeSpeaker]?.name ?: "Studio Natural Voice"
                )

                if (lineChanged) {
                    speakCurrentLine()
                }
            }
        }
    }

    private fun startWaveformAnimation() {
        waveformJob?.cancel()
        waveformJob = scope.launch(Dispatchers.Default) {
            var step = 0
            while (isActive && _state.value.isPlaying) {
                step++
                val newAmps = List(16) { i ->
                    val phase = (step * 0.4f) + (i * 0.6f)
                    val base = (kotlin.math.sin(phase) * 0.5f + 0.5f).toFloat()
                    (0.15f + base * 0.85f).coerceIn(0.1f, 1.0f)
                }
                _state.value = _state.value.copy(waveformAmplitudes = newAmps)
                delay(80)
            }
        }
    }

    private fun speakCurrentLine() {
        if (!_state.value.isTtsReady || tts == null) return
        val currentLine = RadioShowRepository.transcriptLines.getOrNull(_state.value.currentLineIndex) ?: return
        val speaker = RadioShowRepository.speakers[currentLine.speakerId] ?: return
        val currentTts = tts ?: return

        try {
            currentTts.stop()

            // 1. Assign voice model if available
            val dedicatedVoice = speakerVoiceMap[speaker.id]
            if (dedicatedVoice != null) {
                try {
                    currentTts.voice = dedicatedVoice
                } catch (_: Exception) {}
            }

            // 2. Adjust acoustic modulation & pacing
            val tunedPitch = when (speaker.id) {
                "paul" -> 0.90f // Anchored broadcast host
                "sarah" -> 1.15f // Clear analytical specialist
                "leo" -> 1.04f  // Conversational tech lead
                "mark" -> 0.94f // Veteran systems engineer
                else -> speaker.speechPitch
            }

            val tunedRate = when (speaker.id) {
                "paul" -> 0.96f
                "sarah" -> 1.02f
                "leo" -> 1.06f
                "mark" -> 0.98f
                else -> speaker.speechRate
            }

            currentTts.setPitch(tunedPitch)
            currentTts.setSpeechRate(tunedRate * _state.value.playbackSpeed)

            val utteranceId = "line_${currentLine.index}_${System.currentTimeMillis()}"
            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            }

            currentTts.speak(currentLine.dialogue, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        } catch (_: Exception) {
            // TTS gracefully handles exceptions without crashing playback loop
        }
    }

    fun release() {
        pause()
        tts?.shutdown()
        tts = null
    }
}
