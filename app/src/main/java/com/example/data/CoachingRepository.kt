package com.example.data

import com.example.model.CoachingSession
import kotlinx.coroutines.flow.Flow

class CoachingRepository(private val dao: CoachingDao) {
  val allSessions: Flow<List<CoachingSession>> = dao.getAllSessions()

  fun getSessionById(id: Long): Flow<CoachingSession?> = dao.getSessionById(id)

  suspend fun insertSession(session: CoachingSession): Long = dao.insertSession(session)

  suspend fun insertSessions(sessions: List<CoachingSession>) = dao.insertSessions(sessions)

  suspend fun updateSession(session: CoachingSession) = dao.updateSession(session)

  suspend fun deleteSession(session: CoachingSession) = dao.deleteSession(session)

  suspend fun deleteSessionById(id: Long) = dao.deleteSessionById(id)

  suspend fun clearAll() = dao.clearAll()

  suspend fun getCount(): Int = dao.getCount()
}
