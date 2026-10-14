# ClipForge AI - Viral YouTube Video Clipper

ClipForge AI transforms long-form YouTube videos into high-engagement, scroll-stopping 9:16 vertical shorts for TikTok, Instagram Reels, and YouTube Shorts using AI-powered viral moment detection.

## User Review & Critical Decisions

> [!IMPORTANT]
> The core workflow and feature set have been shaped directly by your confirmed preferences:
> - **AI Hook Detection**: We will use Gemini 3.5 Flash to analyze video transcripts, content, and pacing to pinpoint high-retention viral moments with exact start/end timestamps.
> - **9:16 Vertical Format with Dynamic Captions**: Clips are formatted in 9:16 vertical canvas with styled animated word captions (MrBeast/Hormozi style high-contrast highlights).
> - **Virality Engine**: Every clip is rated with a 0-100 Viral Potential Score, scroll-stopping hook titles, retention breakdowns, and trending hashtags.

- **Confirmed Decision 1**: AI transcript & moment analysis to automatically detect hooks, emotional climaxes, and key insights.
- **Confirmed Decision 2**: 9:16 vertical shorts styling with dynamic, customizable caption overlays and safe-zone guides.
- **Confirmed Decision 3**: Deep virality intelligence: viral potential scores, algorithmic retention tips, hook titles, and ready-to-copy hashtag sets.
- **Persistence Choice**: Room Database for local clip storage, draft trimming, and favorite collections.

---

## 1. Overview & Core Concept

- **What It Does**: The user pastes any YouTube link (or selects a trending sample). ClipForge AI inspects the video, extracts its transcript/narrative structure, and uses Gemini AI to identify 3–6 top viral clip moments. Users can preview each clip in a 9:16 vertical player, fine-tune trim timestamps, toggle animated caption themes, inspect virality analytics, and save or export clips with optimized captions and hashtags.
- **Target Audience / Persona**: Content creators, short-form editors, podcasters, marketers, and social media managers repurposing long-form content for TikTok, Shorts, and Reels.
- **Key Value**: Eliminates hours of manual video scrubbing and editing by immediately surfacing the highest-converting 30–60 second segments with ready-to-post hooks.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Dashboard & Import**:
   - Clean, modern Creator Studio dashboard.
   - Quick-paste URL bar with automatic YouTube link validation.
   - "Try Sample" chips featuring iconic podcasts, keynotes, and interviews for instant testing.
   - Recent projects & quick statistics (total clips created, average viral score).

2. **AI Analysis & Viral Moments Feed**:
   - Polished analysis animation showing progress (Extracting transcript → Detecting hooks → Scoring virality).
   - Grid/list of generated viral clips, each featuring:
     - Viral Potential Score pill (e.g., `🔥 94/100 Viral Score`)
     - Punchy Hook Title (e.g., "The 3-Second Rule That Changed Everything")
     - Exact timestamp duration (e.g., `01:24 - 02:08 • 44s`)
     - Why It Works badge (e.g., "Strong Pattern Interrupt + Emotional Peak")

3. **9:16 Vertical Studio & Preview Editor**:
   - Full 9:16 mobile canvas simulation with real-time video playback and timestamp seeking.
   - Live dynamic caption overlays with switchable presets:
     - *Hormozi Bold* (yellow/white punch font with solid shadow)
     - *MrBeast Impact* (bouncy high-contrast pop highlights)
     - *Neon Glow* (cyberpunk cyan/magenta aesthetic)
     - *Minimal Modern* (clean understated subtitle bar)
   - Interactive timeline scrubber with draggable start and end handles.
   - Overlay toggles: Top Hook Banner, Platform Safe Zones (TikTok/Reels UI overlay simulator).

4. **Viral Growth & Export Sheet**:
   - One-tap copy for TikTok/Reels descriptions with generated hook + trending hashtags.
   - Virality breakdown cards (Hook Retention, Pacing, Emotional Impact).
   - "Save to My Clips" and "Share Clip Package" intents.

5. **My Clips Library**:
   - Saved clips organized by source video and viral score.
   - Quick preview, edit trim, and export capabilities.

### Visual Identity & Theme

- **Aesthetic Direction**: *Midnight Creator Studio* — sleek, modern, high-contrast dark UI tailored for video creators.
- **Color Palette**:
  - Background: Deep obsidian (`#0A0D14`) & studio surface (`#121622`)
  - Primary Accent: Electric Violet / Neon Purple (`#8B5CF6`)
  - Secondary Accent: Cyber Cyan (`#06B6D4`)
  - High Virality Accent: Burning Flame Amber/Orange (`#F97316`)
  - Text: High-contrast crisp white (`#F8FAFC`) with muted slate secondary (`#94A3B8`)
- **Typography & Hierarchy**: Clean, ultra-readable modern sans-serif typography with heavy display weights for viral hook titles and stats.
- **Interactive Feedback**: Spring animations on clip cards, score meters with gradient fills, smooth bottom sheets, and haptic feedback triggers.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct Gemini REST API for Instant Intelligence**
  - *Chosen Approach*: Direct Retrofit calls to Gemini 3.5 Flash using structured JSON responses for prompt, transcript, and viral moment scoring.
  - *Why*: Delivers sub-3-second inference with high accuracy in pinpointing narrative arcs, pattern interrupts, and timestamps without requiring backend server setup.
- **Decision 2: Interactive 9:16 Studio Player with YouTube Frame & Fallback Canvas**
  - *Chosen Approach*: Hybrid preview player that combines an interactive embedded YouTube player synced to precise timestamps with an overlay rendering layer for dynamic 9:16 vertical crop, animated subtitles, and safe zones.
  - *Why*: Allows creators to immediately inspect the exact video segment while seeing what it looks like cropped to 9:16 with caption overlays.
- **Decision 3: Room Database for Local Persistence**
  - *Chosen Approach*: Local SQLite storage via Room for saved clips, draft edits, and video history.
  - *Why*: Fast, zero-friction, private, and works completely offline once clips are generated.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      ClipForge AI                           │
│                                                             │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 UI Layer (Compose)                  │   │
│   │  ┌──────────────┐ ┌───────────────┐ ┌────────────┐  │   │
│   │  │ Home/Import  │ │ Clip Feed &   │ │ 9:16 Studio│  │   │
│   │  │ Screen       │ │ Viral Scoring │ │ & Player   │  │   │
│   │  └──────┬───────┘ └───────┬───────┘ └─────┬──────┘  │   │
│   │         │                 │               │         │   │
│   │         └─────────────────┼───────────────┘         │   │
│   │                           ▼                         │   │
│   │                 ClipForgeViewModel                  │   │
│   └───────────────────────────┬─────────────────────────┘   │
│                               │                             │
│   ┌───────────────────────────┼─────────────────────────┐   │
│   │ Domain & Data Layer       ▼                         │   │
│   │  ┌──────────────────────────────┐                   │   │
│   │  │   ClipRepository             │                   │   │
│   │  └──────┬────────────────┬──────┘                   │   │
│   │         │                │                          │   │
│   │         ▼                ▼                          │   │
│   │  ┌──────────────┐ ┌──────────────┐                  │   │
│   │  │ GeminiService│ │ Room Database│                  │   │
│   │  │ (REST API)   │ │ (Clips, URLs)│                  │   │
│   │  └──────────────┘ └──────────────┘                  │   │
│   └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Data Models

```kotlin
data class ViralClip(
    val id: String,
    val youtubeUrl: String,
    val videoTitle: String,
    val thumbnailUrl: String,
    val hookTitle: String,
    val startTimeSeconds: Int,
    val endTimeSeconds: Int,
    val viralScore: Int, // 0 - 100
    val viralityReason: String,
    val hashtags: List<String>,
    val transcriptSnippet: String,
    val captionTheme: String,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

### Planned Development Steps

1. Configure environment and dependencies: Enable `GEMINI_API_KEY` in `.env.example`, ensure Kotlin serialization and OkHttp timeouts (60s) are configured in `build.gradle.kts`.
2. Implement Gemini AI Client: Create structured prompt schemas to extract viral timestamps, hook titles, retention analysis, and captions from YouTube URLs and content.
3. Setup Room Database & Repository: Define tables for generated clips, presets, and history.
4. Build Creator UI & Components:
   - Navigation & TopBar with studio styling
   - URL Input bar with quick paste & curated YouTube samples
   - Viral Clip cards with score badges and hook previews
   - 9:16 Vertical Video Studio with timestamp trimmer, animated captions, and platform safe zone toggles
   - Export & Copy modal with viral captions, hashtags, and virality breakdown
   - "My Clips" saved library
5. Test, compile, and verify with `compile_applet`.
