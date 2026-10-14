package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "viral_clips")
data class ViralClip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val videoId: String,
    val youtubeUrl: String,
    val videoTitle: String,
    val channelTitle: String = "YouTube Creator",
    val thumbnailUrl: String,
    val hookTitle: String,
    val startTimeSeconds: Int,
    val endTimeSeconds: Int,
    val viralScore: Int, // 0 - 100
    val viralityReason: String,
    val hashtags: String, // Comma-separated tags
    val captionSnippet: String,
    val captionStyle: String = "HORMOZI_BOLD",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val durationSeconds: Int
        get() = (endTimeSeconds - startTimeSeconds).coerceAtLeast(1)

    val formattedDuration: String
        get() = "${durationSeconds}s"

    val formattedTimeRange: String
        get() {
            val startMin = startTimeSeconds / 60
            val startSec = startTimeSeconds % 60
            val endMin = endTimeSeconds / 60
            val endSec = endTimeSeconds % 60
            return String.format("%02d:%02d - %02d:%02d", startMin, startSec, endMin, endSec)
        }

    val tagList: List<String>
        get() = hashtags.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { if (it.startsWith("#")) it else "#$it" }
}
