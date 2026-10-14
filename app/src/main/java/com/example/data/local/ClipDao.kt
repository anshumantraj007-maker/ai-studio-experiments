package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ViralClip
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipDao {
    @Query("SELECT * FROM viral_clips ORDER BY createdAt DESC")
    fun getAllClips(): Flow<List<ViralClip>>

    @Query("SELECT * FROM viral_clips WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteClips(): Flow<List<ViralClip>>

    @Query("SELECT * FROM viral_clips WHERE id = :id LIMIT 1")
    fun getClipById(id: String): Flow<ViralClip?>

    @Query("SELECT * FROM viral_clips WHERE videoId = :videoId ORDER BY viralScore DESC")
    fun getClipsForVideo(videoId: String): Flow<List<ViralClip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: ViralClip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClips(clips: List<ViralClip>)

    @Update
    suspend fun updateClip(clip: ViralClip)

    @Delete
    suspend fun deleteClip(clip: ViralClip)

    @Query("DELETE FROM viral_clips WHERE id = :id")
    suspend fun deleteClipById(id: String)
}
