package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.remote.YouTubeMetadataHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ClipForge AI", appName)
  }

  @Test
  fun `extract youtube video id correctly`() {
    val url1 = "https://www.youtube.com/watch?v=UF8uR6Z6KLc"
    val id1 = YouTubeMetadataHelper.extractVideoId(url1)
    assertEquals("UF8uR6Z6KLc", id1)

    val url2 = "https://youtu.be/kRj8L9w1rEQ"
    val id2 = YouTubeMetadataHelper.extractVideoId(url2)
    assertEquals("kRj8L9w1rEQ", id2)

    val url3 = "https://www.youtube.com/shorts/cKxRvEZd3Mw"
    val id3 = YouTubeMetadataHelper.extractVideoId(url3)
    assertEquals("cKxRvEZd3Mw", id3)
  }
}
