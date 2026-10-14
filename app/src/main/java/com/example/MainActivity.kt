package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StudioBottomNavigationBar
import com.example.ui.components.StudioTopBar
import com.example.ui.components.ToastNotificationBanner
import com.example.ui.screens.AnalysisProgressScreen
import com.example.ui.screens.ClipsFeedScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyClipsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.ClipForgeTheme
import com.example.ui.theme.StudioDarkBg
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ClipForgeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ClipForgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClipForgeTheme {
                ClipForgeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ClipForgeApp(viewModel: ClipForgeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedClips by viewModel.savedClips.collectAsStateWithLifecycle()

    val savedClipIds = remember(savedClips) {
        savedClips.map { it.id }.toSet()
    }

    // System back navigation handling
    BackHandler(enabled = uiState.currentScreen != AppScreen.HOME) {
        when (uiState.currentScreen) {
            AppScreen.STUDIO -> {
                if (uiState.generatedClips.isNotEmpty()) {
                    viewModel.navigateTo(AppScreen.CLIPS_FEED)
                } else {
                    viewModel.navigateTo(AppScreen.HOME)
                }
            }
            AppScreen.CLIPS_FEED -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.MY_SAVED_CLIPS -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.ANALYZING -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.HOME -> { /* Exit handled by OS */ }
        }
    }

    val topBarTitle = when (uiState.currentScreen) {
        AppScreen.HOME -> "ClipForge AI"
        AppScreen.ANALYZING -> "AI Viral Scanner"
        AppScreen.CLIPS_FEED -> "Viral Moments"
        AppScreen.STUDIO -> "9:16 Shorts Studio"
        AppScreen.MY_SAVED_CLIPS -> "Saved Library"
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg),
        topBar = {
            if (uiState.currentScreen != AppScreen.ANALYZING) {
                StudioTopBar(
                    title = topBarTitle,
                    currentScreen = uiState.currentScreen,
                    savedClipsCount = savedClips.size,
                    onNavigateBack = {
                        when (uiState.currentScreen) {
                            AppScreen.STUDIO -> {
                                if (uiState.generatedClips.isNotEmpty()) {
                                    viewModel.navigateTo(AppScreen.CLIPS_FEED)
                                } else {
                                    viewModel.navigateTo(AppScreen.HOME)
                                }
                            }
                            AppScreen.CLIPS_FEED -> viewModel.navigateTo(AppScreen.HOME)
                            AppScreen.MY_SAVED_CLIPS -> viewModel.navigateTo(AppScreen.HOME)
                            else -> viewModel.navigateTo(AppScreen.HOME)
                        }
                    },
                    onOpenSavedClips = {
                        viewModel.navigateTo(AppScreen.MY_SAVED_CLIPS)
                    }
                )
            }
        },
        bottomBar = {
            if (uiState.currentScreen != AppScreen.ANALYZING) {
                StudioBottomNavigationBar(
                    currentScreen = uiState.currentScreen,
                    savedClipsCount = savedClips.size,
                    hasGeneratedClips = uiState.generatedClips.isNotEmpty(),
                    onNavigate = { targetScreen ->
                        viewModel.navigateTo(targetScreen)
                    }
                )
            }
        },
        containerColor = StudioDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        inputUrl = uiState.inputUrl,
                        userNotes = uiState.userNotes,
                        errorMessage = uiState.errorMessage,
                        savedClipsCount = savedClips.size,
                        onUrlChange = { viewModel.onUrlChange(it) },
                        onNotesChange = { viewModel.onNotesChange(it) },
                        onSelectSample = { viewModel.selectSample(it) },
                        onStartAnalysis = { viewModel.startAnalysis() },
                        onOpenLibrary = { viewModel.navigateTo(AppScreen.MY_SAVED_CLIPS) }
                    )
                }

                AppScreen.ANALYZING -> {
                    AnalysisProgressScreen(
                        currentStep = uiState.analysisStep,
                        progress = uiState.analysisProgress
                    )
                }

                AppScreen.CLIPS_FEED -> {
                    ClipsFeedScreen(
                        videoInfo = uiState.currentVideoInfo,
                        clips = uiState.generatedClips,
                        savedClipsIds = savedClipIds,
                        onOpenStudio = { clip ->
                            viewModel.openInStudio(clip)
                        },
                        onSaveClip = { clip ->
                            viewModel.saveActiveClip()
                        },
                        onNewAnalysis = {
                            viewModel.navigateTo(AppScreen.HOME)
                        }
                    )
                }

                AppScreen.STUDIO -> {
                    StudioScreen(
                        clip = uiState.activeStudioClip,
                        isPlaying = uiState.isPlaying,
                        currentPlaybackTime = uiState.currentPlaybackTime,
                        trimStart = uiState.customTrimStart,
                        trimEnd = uiState.customTrimEnd,
                        captionPreset = uiState.captionPreset,
                        showSafeZones = uiState.showSafeZones,
                        showHookBanner = uiState.showHookBanner,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSeek = { viewModel.seekTo(it) },
                        onUpdateTrim = { start, end -> viewModel.updateTrim(start, end) },
                        onSetCaptionPreset = { viewModel.setCaptionPreset(it) },
                        onToggleSafeZones = { viewModel.toggleSafeZones() },
                        onToggleHookBanner = { viewModel.toggleHookBanner() },
                        onSaveClip = { viewModel.saveActiveClip() },
                        onCopyPackage = { /* feedback handled in studio */ },
                        onNavigateBack = {
                            if (uiState.generatedClips.isNotEmpty()) {
                                viewModel.navigateTo(AppScreen.CLIPS_FEED)
                            } else {
                                viewModel.navigateTo(AppScreen.HOME)
                            }
                        }
                    )
                }

                AppScreen.MY_SAVED_CLIPS -> {
                    MyClipsScreen(
                        savedClips = savedClips,
                        onOpenStudio = { clip ->
                            viewModel.openInStudio(clip)
                        },
                        onToggleFavorite = { clip ->
                            viewModel.toggleFavorite(clip)
                        },
                        onDeleteClip = { clipId ->
                            viewModel.deleteSavedClip(clipId)
                        },
                        onStartNewClip = {
                            viewModel.navigateTo(AppScreen.HOME)
                        },
                        onShowToast = {
                            // toast shown via VM
                        }
                    )
                }
            }

            // Toast overlay
            ToastNotificationBanner(
                message = uiState.toastMessage,
                onDismiss = { viewModel.clearToast() }
            )
        }
    }
}
