package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CoachingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface CoachingDao {
  @Query("SELECT * FROM coaching_sessions ORDER BY dateMillis DESC, id DESC")
  fun getAllSessions(): Flow<List<CoachingSession>>

  @Query("SELECT * FROM coaching_sessions WHERE id = :id LIMIT 1")
  fun getSessionById(id: Long): Flow<CoachingSession?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: CoachingSession): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSessions(sessions: List<CoachingSession>)

  @Update
  suspend fun updateSession(session: CoachingSession)

  @Delete
  suspend fun deleteSession(session: CoachingSession)

  @Query("DELETE FROM coaching_sessions WHERE id = :id")
  suspend fun deleteSessionById(id: Long)

  @Query("DELETE FROM coaching_sessions")
  suspend fun clearAll()

  @Query("SELECT COUNT(*) FROM coaching_sessions")
  suspend fun getCount(): Int
}
