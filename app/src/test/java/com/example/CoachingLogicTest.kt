package com.example

import com.example.model.CoachingMethod
import com.example.model.CoachingSession
import com.example.model.CoachingType
import com.example.model.FeeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoachingLogicTest {

  @Test
  fun testKacProgressCalculations() {
    val targetHours = 50.0f
    val targetMinutes = 3000 // 50 * 60

    val sessions = listOf(
      CoachingSession(
        clientName = "김코치",
        feeType = FeeType.PAID,
        durationMinutes = 60,
        topic = "리더십 코칭",
        startTime = "14:00",
        endTime = "15:00"
      ),
      CoachingSession(
        clientName = "박고객",
        feeType = FeeType.PAID,
        durationMinutes = 90,
        topic = "커리어 전환",
        startTime = "10:00",
        endTime = "11:30"
      ),
      CoachingSession(
        clientName = "이멘티",
        feeType = FeeType.PRO_BONO,
        durationMinutes = 30,
        topic = "학습 동기부여",
        startTime = "19:00",
        endTime = "19:30"
      )
    )

    val totalMinutes = sessions.sumOf { it.durationMinutes }
    assertEquals(180, totalMinutes) // 3 hours

    val totalHours = totalMinutes / 60f
    assertEquals(3.0f, totalHours, 0.001f)

    val progressPercent = ((totalHours / targetHours) * 100f)
    assertEquals(6.0f, progressPercent, 0.001f)

    val remainingMinutes = targetMinutes - totalMinutes
    assertEquals(2820, remainingMinutes)

    val isCompleted = totalHours >= targetHours
    assertFalse(isCompleted)

    val paidMinutes = sessions.filter { it.feeType == FeeType.PAID }.sumOf { it.durationMinutes }
    assertEquals(150, paidMinutes)

    val proBonoMinutes = sessions.filter { it.feeType == FeeType.PRO_BONO }.sumOf { it.durationMinutes }
    assertEquals(30, proBonoMinutes)
  }

  @Test
  fun test50HoursMilestoneCompletion() {
    val totalMinutes = 3060 // 51 hours
    val totalHours = totalMinutes / 60f
    val targetHours = 50.0f
    val isCompleted = totalHours >= targetHours
    assertTrue(isCompleted)
  }

  @Test
  fun testCoachingEnums() {
    assertEquals("유료", FeeType.PAID.displayName)
    assertEquals("무료/프로보노", FeeType.PRO_BONO.displayName)
    assertEquals("1:1 개인", CoachingType.ONE_ON_ONE.displayName)
    assertEquals("대면", CoachingMethod.FACE_TO_FACE.displayName)
    assertEquals("화상", CoachingMethod.VIDEO.displayName)
    assertEquals("전화", CoachingMethod.CALL.displayName)
  }

  @Test
  fun testStopwatchLapCalculations() {
    val elapsed1 = 600000L // 10 mins
    val elapsed2 = 1500000L // 25 mins total (+15 mins)

    val lap1Duration = elapsed1
    val lap2Duration = elapsed2 - elapsed1

    assertEquals(600000L, lap1Duration)
    assertEquals(900000L, lap2Duration)
  }
}
