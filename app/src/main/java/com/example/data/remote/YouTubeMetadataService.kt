package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.regex.Pattern

data class YouTubeVideoInfo(
    val videoId: String,
    val title: String,
    val author: String,
    val thumbnailUrl: String,
    val canonicalUrl: String
)

object YouTubeMetadataHelper {
    private val client = OkHttpClient.Builder().build()

    // Regular expression matching all typical YouTube URL formats
    private val YOUTUBE_PATTERN = Pattern.compile(
        "(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtube\\.com\\/(?:watch\\?v=|shorts\\/|embed\\/)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})"
    )

    fun extractVideoId(url: String): String? {
        val trimmed = url.trim()
        val matcher = YOUTUBE_PATTERN.matcher(trimmed)
        return if (matcher.find()) {
            matcher.group(1)
        } else if (trimmed.length == 11 && trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            trimmed
        } else {
            null
        }
    }

    suspend fun fetchVideoDetails(videoId: String): YouTubeVideoInfo = withContext(Dispatchers.IO) {
        val canonicalUrl = "https://www.youtube.com/watch?v=$videoId"
        val fallbackThumbnail = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        val oEmbedUrl = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"

        try {
            val request = Request.Builder()
                .url(oEmbedUrl)
                .header("User-Agent", "ClipForge-App/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        val json = JSONObject(body)
                        val title = json.optString("title", "YouTube Video")
                        val author = json.optString("author_name", "YouTube Creator")
                        val thumb = json.optString("thumbnail_url", fallbackThumbnail)
                        return@withContext YouTubeVideoInfo(
                            videoId = videoId,
                            title = title,
                            author = author,
                            thumbnailUrl = thumb,
                            canonicalUrl = canonicalUrl
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore network errors and return best effort metadata
        }

        YouTubeVideoInfo(
            videoId = videoId,
            title = "YouTube Video ($videoId)",
            author = "YouTube Creator",
            thumbnailUrl = fallbackThumbnail,
            canonicalUrl = canonicalUrl
        )
    }
}
