package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CaptionStylePreset
import com.example.data.model.ViralClip
import com.example.data.model.YouTubeSample
import com.example.data.model.YouTubeSamples
import com.example.data.remote.YouTubeVideoInfo
import com.example.data.repository.ClipRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    ANALYZING,
    CLIPS_FEED,
    STUDIO,
    MY_SAVED_CLIPS
}

data class ClipForgeUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val inputUrl: String = "",
    val userNotes: String = "",
    val isAnalyzing: Boolean = false,
    val analysisStep: String = "",
    val analysisProgress: Float = 0f,
    val currentVideoInfo: YouTubeVideoInfo? = null,
    val generatedClips: List<ViralClip> = emptyList(),
    val activeStudioClip: ViralClip? = null,
    val isPlaying: Boolean = false,
    val currentPlaybackTime: Float = 0f,
    val customTrimStart: Int = 0,
    val customTrimEnd: Int = 0,
    val captionPreset: CaptionStylePreset = CaptionStylePreset.HORMOZI_BOLD,
    val showSafeZones: Boolean = false,
    val showHookBanner: Boolean = true,
    val errorMessage: String? = null,
    val toastMessage: String? = null
)

class ClipForgeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClipRepository
    private var playbackJob: Job? = null

    private val _uiState = MutableStateFlow(ClipForgeUiState())
    val uiState: StateFlow<ClipForgeUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ClipRepository(database.clipDao())
    }

    val savedClips: StateFlow<List<ViralClip>> = repository.savedClips
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteClips: StateFlow<List<ViralClip>> = repository.favoriteClips
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onUrlChange(newUrl: String) {
        _uiState.update { it.copy(inputUrl = newUrl, errorMessage = null) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(userNotes = notes) }
    }

    fun selectSample(sample: YouTubeSample) {
        _uiState.update {
            it.copy(
                inputUrl = sample.url,
                errorMessage = null
            )
        }
        startAnalysis()
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun startAnalysis() {
        val url = _uiState.value.inputUrl.trim()
        if (url.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter or paste a YouTube video link") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAnalyzing = true,
                    currentScreen = AppScreen.ANALYZING,
                    analysisProgress = 0.15f,
                    analysisStep = "Inspecting YouTube video metadata...",
                    errorMessage = null
                )
            }

            delay(700)
            _uiState.update {
                it.copy(
                    analysisProgress = 0.40f,
                    analysisStep = "Extracting narrative pacing & transcript hooks..."
                )
            }

            delay(800)
            _uiState.update {
                it.copy(
                    analysisProgress = 0.70f,
                    analysisStep = "Gemini AI detecting viral hooks & retention spikes..."
                )
            }

            val result = repository.analyzeYouTubeUrl(url, _uiState.value.userNotes)

            delay(600)
            _uiState.update {
                it.copy(
                    analysisProgress = 0.95f,
                    analysisStep = "Generating 9:16 vertical captions & viral scoring..."
                )
            }
            delay(400)

            result.onSuccess { (videoInfo, clips) ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        currentVideoInfo = videoInfo,
                        generatedClips = clips,
                        currentScreen = AppScreen.CLIPS_FEED,
                        toastMessage = "Extracted ${clips.size} viral clip moments!"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        currentScreen = AppScreen.HOME,
                        errorMessage = error.localizedMessage ?: "Failed to analyze YouTube video."
                    )
                }
            }
        }
    }

    fun openInStudio(clip: ViralClip) {
        val preset = try {
            CaptionStylePreset.valueOf(clip.captionStyle)
        } catch (_: Exception) {
            CaptionStylePreset.HORMOZI_BOLD
        }

        _uiState.update {
            it.copy(
                activeStudioClip = clip,
                customTrimStart = clip.startTimeSeconds,
                customTrimEnd = clip.endTimeSeconds,
                currentPlaybackTime = clip.startTimeSeconds.toFloat(),
                captionPreset = preset,
                currentScreen = AppScreen.STUDIO,
                isPlaying = false
            )
        }
    }

    fun togglePlayPause() {
        val isCurrentlyPlaying = _uiState.value.isPlaying
        if (isCurrentlyPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        playbackJob?.cancel()
        _uiState.update { it.copy(isPlaying = true) }

        val start = _uiState.value.customTrimStart.toFloat()
        val end = _uiState.value.customTrimEnd.toFloat()

        if (_uiState.value.currentPlaybackTime >= end || _uiState.value.currentPlaybackTime < start) {
            _uiState.update { it.copy(currentPlaybackTime = start) }
        }

        playbackJob = viewModelScope.launch {
            while (_uiState.value.isPlaying) {
                delay(100)
                val current = _uiState.value.currentPlaybackTime + 0.1f
                val trimEnd = _uiState.value.customTrimEnd.toFloat()
                val trimStart = _uiState.value.customTrimStart.toFloat()

                if (current >= trimEnd) {
                    _uiState.update { it.copy(currentPlaybackTime = trimStart) }
                } else {
                    _uiState.update { it.copy(currentPlaybackTime = current) }
                }
            }
        }
    }

    fun pausePlayback() {
        playbackJob?.cancel()
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun seekTo(timeSeconds: Float) {
        val clamped = timeSeconds.coerceIn(
            _uiState.value.customTrimStart.toFloat(),
            _uiState.value.customTrimEnd.toFloat()
        )
        _uiState.update { it.copy(currentPlaybackTime = clamped) }
    }

    fun updateTrim(startSec: Int, endSec: Int) {
        val validStart = startSec.coerceAtLeast(0)
        val validEnd = endSec.coerceAtLeast(validStart + 5)
        _uiState.update {
            it.copy(
                customTrimStart = validStart,
                customTrimEnd = validEnd,
                currentPlaybackTime = it.currentPlaybackTime.coerceIn(validStart.toFloat(), validEnd.toFloat())
            )
        }
    }

    fun setCaptionPreset(preset: CaptionStylePreset) {
        _uiState.update { it.copy(captionPreset = preset) }
    }

    fun toggleSafeZones() {
        _uiState.update { it.copy(showSafeZones = !it.showSafeZones) }
    }

    fun toggleHookBanner() {
        _uiState.update { it.copy(showHookBanner = !it.showHookBanner) }
    }

    fun saveActiveClip() {
        val active = _uiState.value.activeStudioClip ?: return
        val customizedClip = active.copy(
            startTimeSeconds = _uiState.value.customTrimStart,
            endTimeSeconds = _uiState.value.customTrimEnd,
            captionStyle = _uiState.value.captionPreset.name
        )

        viewModelScope.launch {
            repository.saveClip(customizedClip)
            _uiState.update {
                it.copy(toastMessage = "Clip saved to My Library!")
            }
        }
    }

    fun toggleFavorite(clip: ViralClip) {
        viewModelScope.launch {
            repository.toggleFavorite(clip)
        }
    }

    fun deleteSavedClip(clipId: String) {
        viewModelScope.launch {
            repository.deleteClip(clipId)
            _uiState.update {
                it.copy(toastMessage = "Clip removed.")
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}
