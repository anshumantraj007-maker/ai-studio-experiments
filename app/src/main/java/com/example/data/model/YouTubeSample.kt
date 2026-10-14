package com.example.data.model

data class YouTubeSample(
    val title: String,
    val author: String,
    val category: String,
    val url: String,
    val videoId: String,
    val thumbnailUrl: String,
    val summary: String
)

object YouTubeSamples {
    val list = listOf(
        YouTubeSample(
            title = "Steve Jobs' 2005 Stanford Commencement Address",
            author = "Stanford",
            category = "Inspiration & Life",
            url = "https://www.youtube.com/watch?v=UF8uR6Z6KLc",
            videoId = "UF8uR6Z6KLc",
            thumbnailUrl = "https://img.youtube.com/vi/UF8uR6Z6KLc/hqdefault.jpg",
            summary = "How connecting the dots, love and loss, and mortality shaped the greatest technology visionary."
        ),
        YouTubeSample(
            title = "Alex Hormozi: The Secret to Scaling Beyond $100M",
            author = "Alex Hormozi",
            category = "Business & Wealth",
            url = "https://www.youtube.com/watch?v=kRj8L9w1rEQ",
            videoId = "kRj8L9w1rEQ",
            thumbnailUrl = "https://img.youtube.com/vi/kRj8L9w1rEQ/hqdefault.jpg",
            summary = "High-retention business psychology, leverage principles, and volume-based marketing."
        ),
        YouTubeSample(
            title = "MrBeast Reveals How to Go Viral on YouTube",
            author = "PowerfulJRE",
            category = "Creator Secrets",
            url = "https://www.youtube.com/watch?v=D-yphqVIdg0",
            videoId = "D-yphqVIdg0",
            thumbnailUrl = "https://img.youtube.com/vi/D-yphqVIdg0/hqdefault.jpg",
            summary = "The exact algorithm secrets, thumbnail pacing, and 3-second hook metrics."
        ),
        YouTubeSample(
            title = "Jensen Huang: Nvidia's Unconventional Secret to Success",
            author = "Stanford Graduate School of Business",
            category = "Tech & AI",
            url = "https://www.youtube.com/watch?v=cKxRvEZd3Mw",
            videoId = "cKxRvEZd3Mw",
            thumbnailUrl = "https://img.youtube.com/vi/cKxRvEZd3Mw/hqdefault.jpg",
            summary = "Why high pain tolerance and grit matter more than raw intellect when building trillion-dollar tech."
        )
    )
}
