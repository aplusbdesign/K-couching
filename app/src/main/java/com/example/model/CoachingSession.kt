package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CoachingType(val displayName: String) {
  ONE_ON_ONE("1:1 개인"),
  GROUP("그룹 코칭");

  companion object {
    fun fromName(name: String?): CoachingType =
      entries.find { it.name.equals(name, ignoreCase = true) } ?: ONE_ON_ONE
  }
}

enum class FeeType(val displayName: String) {
  PAID("유료"),
  PRO_BONO("무료/프로보노");

  companion object {
    fun fromName(name: String?): FeeType =
      entries.find { it.name.equals(name, ignoreCase = true) } ?: PAID
  }
}

enum class CoachingMethod(val displayName: String) {
  FACE_TO_FACE("대면"),
  VIDEO("화상"),
  CALL("전화");

  companion object {
    fun fromName(name: String?): CoachingMethod =
      entries.find { it.name.equals(name, ignoreCase = true) } ?: FACE_TO_FACE
  }
}

@Entity(tableName = "coaching_sessions")
data class CoachingSession(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val clientName: String,
  val clientContact: String = "",
  val coachingType: CoachingType = CoachingType.ONE_ON_ONE,
  val groupMemberCount: Int = 1,
  val feeType: FeeType = FeeType.PAID,
  val coachingMethod: CoachingMethod = CoachingMethod.FACE_TO_FACE,
  val dateMillis: Long = System.currentTimeMillis(),
  val startTime: String, // e.g. "14:00"
  val endTime: String,   // e.g. "15:00"
  val durationMinutes: Int, // e.g. 60
  val sessionRound: Int = 1, // e.g. 1회차
  val topic: String, // 코칭 주제 / 목표
  val coachReflection: String = "", // 코치 성찰 / 핵심 질문 / 합의사항
  val createdAt: Long = System.currentTimeMillis()
)
