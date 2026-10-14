package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.CaptionStylePreset
import com.example.data.model.ViralClip
import com.example.ui.components.ViralScoreBadge
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletLight
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ViralFlame
import com.example.ui.theme.ViralFlameLight

@Composable
fun StudioScreen(
    clip: ViralClip?,
    isPlaying: Boolean,
    currentPlaybackTime: Float,
    trimStart: Int,
    trimEnd: Int,
    captionPreset: CaptionStylePreset,
    showSafeZones: Boolean,
    showHookBanner: Boolean,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onUpdateTrim: (Int, Int) -> Unit,
    onSetCaptionPreset: (CaptionStylePreset) -> Unit,
    onToggleSafeZones: () -> Unit,
    onToggleHookBanner: () -> Unit,
    onSaveClip: () -> Unit,
    onCopyPackage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (clip == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(StudioDarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No clip selected for preview.", color = TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onNavigateBack) {
                    Text("Select a Clip")
                }
            }
        }
        return
    }

    var useEmbeddedPlayer by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top score bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ViralScoreBadge(score = clip.viralScore)

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioSurface)
                        .clickable { useEmbeddedPlayer = !useEmbeddedPlayer }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Player Mode",
                        tint = if (useEmbeddedPlayer) CyberCyan else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (useEmbeddedPlayer) "YouTube Web Player" else "Studio Canvas Mode",
                        color = if (useEmbeddedPlayer) CyberCyan else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // The 9:16 Vertical Preview Studio Canvas
        item {
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black)
                    .border(2.dp, StudioBorder, RoundedCornerShape(24.dp))
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .testTag("vertical_canvas_9_16")
            ) {
                if (useEmbeddedPlayer) {
                    // Real Embedded YouTube Player
                    YouTubeEmbeddedWebView(
                        videoId = clip.videoId,
                        startSeconds = trimStart,
                        endSeconds = trimEnd,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Synchronized Studio Video Canvas with Dynamic Captions
                    StudioCanvasPlayer(
                        clip = clip,
                        currentPlaybackTime = currentPlaybackTime,
                        trimStart = trimStart,
                        trimEnd = trimEnd,
                        captionPreset = captionPreset,
                        showHookBanner = showHookBanner,
                        isPlaying = isPlaying,
                        onTogglePlay = onTogglePlayPause,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // TikTok / Instagram Reels Safe Zones Overlay
                if (showSafeZones) {
                    SocialPlatformSafeZonesOverlay(modifier = Modifier.fillMaxSize())
                }
            }
        }

        // Playback Controls Deck
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Scrubbing Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatSeconds(currentPlaybackTime.toInt()),
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Duration: ${trimEnd - trimStart}s",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = formatSeconds(trimEnd),
                            color = TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Slider(
                        value = currentPlaybackTime,
                        onValueChange = onSeek,
                        valueRange = trimStart.toFloat()..trimEnd.toFloat().coerceAtLeast(trimStart.toFloat() + 1f),
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = ElectricViolet,
                            inactiveTrackColor = StudioBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("timeline_slider")
                    )

                    // Transport buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSeek(trimStart.toFloat()) },
                            modifier = Modifier
                                .size(38.dp)
                                .background(StudioSurfaceElevated, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Restart",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Big Play / Pause button
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(ElectricViolet, CyberCyan))
                                )
                                .clickable { onTogglePlayPause() }
                                .testTag("studio_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Precision Trim Adjuster
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Trim Timestamps",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start Trim Box
                        TrimAdjusterBox(
                            label = "Start Time",
                            seconds = trimStart,
                            onDecrease = { onUpdateTrim((trimStart - 1).coerceAtLeast(0), trimEnd) },
                            onIncrease = { onUpdateTrim((trimStart + 1).coerceAtMost(trimEnd - 5), trimEnd) },
                            modifier = Modifier.weight(1f)
                        )

                        // End Trim Box
                        TrimAdjusterBox(
                            label = "End Time",
                            seconds = trimEnd,
                            onDecrease = { onUpdateTrim(trimStart, (trimEnd - 1).coerceAtLeast(trimStart + 5)) },
                            onIncrease = { onUpdateTrim(trimStart, trimEnd + 1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Caption Style Presets Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Animated Caption Style",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose dynamic word animations that boost retention",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CaptionStylePreset.values()) { preset ->
                            val isSelected = preset == captionPreset
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSetCaptionPreset(preset) },
                                label = {
                                    Text(
                                        text = preset.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricViolet,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White,
                                    containerColor = StudioSurfaceElevated,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = StudioBorder,
                                    selectedBorderColor = ElectricVioletLight,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Toggles: Safe Zones & Hook Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = StudioSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Safe zones toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "TikTok / Reels Safe Zones",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Simulate UI overlay buttons & margins",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = showSafeZones,
                            onCheckedChange = { onToggleSafeZones() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CyberCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = StudioBorder
                            ),
                            modifier = Modifier.testTag("safe_zones_switch")
                        )
                    }

                    // Top Hook Banner toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Title,
                                contentDescription = null,
                                tint = ViralFlame,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Top Hook Headline Banner",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Permanent high-converting banner at top",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = showHookBanner,
                            onCheckedChange = { onToggleHookBanner() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ViralFlame,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = StudioBorder
                            ),
                            modifier = Modifier.testTag("hook_banner_switch")
                        )
                    }
                }
            }
        }

        // Export & Save Actions Bar
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Save to Library
                Button(
                    onClick = onSaveClip,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_active_clip_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Clip To My Library",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Copy Social Package (Hook + Description + Hashtags)
                Button(
                    onClick = {
                        val packageText = """
                            🔥 ${clip.hookTitle}
                            
                            ${clip.captionSnippet}
                            
                            Follow for more viral breakdowns!
                            
                            ${clip.hashtags}
                        """.trimIndent()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = android.content.ClipData.newPlainText("Viral Package", packageText)
                        clipboard.setPrimaryClip(clipData)
                        onCopyPackage("Viral description & hashtags copied to clipboard!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("copy_package_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Copy TikTok / Reels Description & Tags",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Share Clip
                Button(
                    onClick = {
                        shareClipPackage(context, clip)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("share_package_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Clip Link Package",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudioCanvasPlayer(
    clip: ViralClip,
    currentPlaybackTime: Float,
    trimStart: Int,
    trimEnd: Int,
    captionPreset: CaptionStylePreset,
    showHookBanner: Boolean,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val duration = (trimEnd - trimStart).coerceAtLeast(1)
    val progressInClip = (currentPlaybackTime - trimStart).coerceIn(0f, duration.toFloat())

    // Split caption snippet lines into timed sentences
    val captionLines = remember(clip.captionSnippet) {
        clip.captionSnippet.split("|").map { it.trim() }.filter { it.isNotEmpty() }
    }

    val activeCaptionIndex = remember(progressInClip, captionLines.size, duration) {
        if (captionLines.isEmpty()) 0
        else {
            val step = duration.toFloat() / captionLines.size
            (progressInClip / step).toInt().coerceIn(0, captionLines.size - 1)
        }
    }

    val activeLine = captionLines.getOrNull(activeCaptionIndex) ?: clip.captionSnippet

    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable { onTogglePlay() }
    ) {
        // Video thumbnail fitted/cropped to vertical canvas
        AsyncImage(
            model = clip.thumbnailUrl,
            contentDescription = clip.videoTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        // Dark cinematic vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.75f),
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // TOP HOOK BANNER
        if (showHookBanner) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 12.dp, end = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                    .border(1.dp, Color(0xFFFACC15).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = clip.hookTitle.uppercase(),
                    color = Color(0xFFFACC15),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // CENTER DYNAMIC ANIMATED CAPTIONS OVERLAY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.Center)
        ) {
            DynamicCaptionBox(
                text = activeLine,
                preset = captionPreset,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // BOTTOM STATUS BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 12.dp, end = 12.dp)
        ) {
            // Audio wave indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = if (isPlaying) CyberCyan else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPlaying) "PLAYING" else "PAUSED",
                        color = if (isPlaying) CyberCyan else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${progressInClip.toInt()}s / ${duration}s",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Thin progress strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (progressInClip / duration).coerceIn(0f, 1f))
                        .height(3.dp)
                        .background(CyberCyan)
                )
            }
        }
    }
}

@Composable
fun DynamicCaptionBox(
    text: String,
    preset: CaptionStylePreset,
    modifier: Modifier = Modifier
) {
    val words = text.split(" ")
    val displayText = if (preset.isUppercase) text.uppercase() else text

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(preset.backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            color = preset.highlightColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun SocialPlatformSafeZonesOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(1.dp, Color(0x66EF4444), RoundedCornerShape(24.dp))
    ) {
        // Top tab header safe zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0x22EF4444))
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Text("SAFE ZONE (TOP NAV)", color = Color(0xAAFFFFFF), fontSize = 8.sp)
        }

        // Right side action bar safe zone (Like, Comment, Share, Sound)
        Column(
            modifier = Modifier
                .width(42.dp)
                .height(160.dp)
                .background(Color(0x33EF4444))
                .align(Alignment.CenterEnd)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LIKE", color = Color(0xAAFFFFFF), fontSize = 7.sp)
            Text("CMT", color = Color(0xAAFFFFFF), fontSize = 7.sp)
            Text("SHR", color = Color(0xAAFFFFFF), fontSize = 7.sp)
            Text("SND", color = Color(0xAAFFFFFF), fontSize = 7.sp)
        }

        // Bottom title / sound disc safe zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Color(0x33EF4444))
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            Text("SAFE ZONE (TIKTOK / REELS CAPTION BOX)", color = Color(0xAAFFFFFF), fontSize = 8.sp)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbeddedWebView(
    videoId: String,
    startSeconds: Int,
    endSeconds: Int,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                setBackgroundColor(0x00000000)

                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                        <style>
                            body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                            iframe { width: 100%; height: 100%; border: none; }
                        </style>
                    </head>
                    <body>
                        <iframe 
                            src="https://www.youtube-nocookie.com/embed/$videoId?start=$startSeconds&end=$endSeconds&autoplay=1&controls=1&rel=0&playsinline=1" 
                            allow="autoplay; encrypted-media; picture-in-picture" 
                            allowfullscreen>
                        </iframe>
                    </body>
                    </html>
                """.trimIndent()
                loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
                        iframe { width: 100%; height: 100%; border: none; }
                    </style>
                </head>
                <body>
                    <iframe 
                        src="https://www.youtube-nocookie.com/embed/$videoId?start=$startSeconds&end=$endSeconds&autoplay=1&controls=1&rel=0&playsinline=1" 
                        allow="autoplay; encrypted-media; picture-in-picture" 
                        allowfullscreen>
                    </iframe>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}

@Composable
fun TrimAdjusterBox(
    label: String,
    seconds: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(StudioSurfaceElevated)
            .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatSeconds(seconds),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier
                        .size(32.dp)
                        .background(StudioSurface, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier
                        .size(32.dp)
                        .background(StudioSurface, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

fun formatSeconds(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return String.format("%02d:%02d", m, s)
}
