package com.example.data.repository

import com.example.data.local.ClipDao
import com.example.data.model.ViralClip
import com.example.data.remote.GeminiService
import com.example.data.remote.YouTubeMetadataHelper
import com.example.data.remote.YouTubeVideoInfo
import kotlinx.coroutines.flow.Flow

class ClipRepository(private val clipDao: ClipDao) {

    val savedClips: Flow<List<ViralClip>> = clipDao.getAllClips()
    val favoriteClips: Flow<List<ViralClip>> = clipDao.getFavoriteClips()

    suspend fun analyzeYouTubeUrl(
        url: String,
        notes: String = ""
    ): Result<Pair<YouTubeVideoInfo, List<ViralClip>>> {
        val videoId = YouTubeMetadataHelper.extractVideoId(url)
            ?: return Result.failure(IllegalArgumentException("Please enter a valid YouTube video link (e.g., https://www.youtube.com/watch?v=... or https://youtu.be/...)"))

        return try {
            val videoInfo = YouTubeMetadataHelper.fetchVideoDetails(videoId)
            val generatedClips = GeminiService.analyzeVideoForViralClips(videoInfo, notes)
            Result.success(Pair(videoInfo, generatedClips))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveClip(clip: ViralClip) {
        clipDao.insertClip(clip)
    }

    suspend fun toggleFavorite(clip: ViralClip) {
        val updated = clip.copy(isFavorite = !clip.isFavorite)
        clipDao.updateClip(updated)
    }

    suspend fun updateClipTrim(clip: ViralClip, newStart: Int, newEnd: Int) {
        val updated = clip.copy(
            startTimeSeconds = newStart,
            endTimeSeconds = newEnd
        )
        clipDao.updateClip(updated)
    }

    suspend fun updateCaptionStyle(clip: ViralClip, style: String) {
        val updated = clip.copy(captionStyle = style)
        clipDao.updateClip(updated)
    }

    suspend fun deleteClip(clipId: String) {
        clipDao.deleteClipById(clipId)
    }
}
