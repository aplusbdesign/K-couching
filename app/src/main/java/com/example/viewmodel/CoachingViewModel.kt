package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CoachingRepository
import com.example.model.CoachingMethod
import com.example.model.CoachingSession
import com.example.model.CoachingType
import com.example.model.FeeType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KacStatistics(
  val totalMinutes: Int = 0,
  val totalHours: Float = 0f,
  val targetHours: Float = 50.0f,
  val progressPercent: Float = 0f,
  val remainingMinutes: Int = 3000, // 50 * 60
  val remainingHours: Float = 50.0f,
  val isCompleted: Boolean = false,
  val paidMinutes: Int = 0,
  val paidHours: Float = 0f,
  val proBonoMinutes: Int = 0,
  val proBonoHours: Float = 0f,
  val oneOnOneMinutes: Int = 0,
  val groupMinutes: Int = 0,
  val faceToFaceMinutes: Int = 0,
  val videoMinutes: Int = 0,
  val callMinutes: Int = 0,
  val totalSessions: Int = 0,
  val uniqueClients: Int = 0,
  val averageDurationMinutes: Int = 0
)

data class LapItem(
  val lapNumber: Int,
  val lapTimeMillis: Long,
  val totalTimeMillis: Long,
  val stageName: String = ""
)

data class TimerUiState(
  val isRunning: Boolean = false,
  val isPaused: Boolean = false,
  val elapsedSeconds: Long = 0L,
  val elapsedMillis: Long = 0L,
  val presetMinutes: Int = 50, // default KAC 50 min session
  val clientName: String = "",
  val topic: String = "",
  val feeType: FeeType = FeeType.PAID,
  val coachingMethod: CoachingMethod = CoachingMethod.VIDEO,
  val coachingType: CoachingType = CoachingType.ONE_ON_ONE,
  val startTimeFormatted: String = "",
  val startTimestamp: Long = 0L,
  val laps: List<LapItem> = emptyList()
)

class CoachingViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: CoachingRepository
  private var timerJob: Job? = null

  init {
    val database = AppDatabase.getDatabase(application, viewModelScope)
    repository = CoachingRepository(database.coachingDao())
  }

  val allSessions: StateFlow<List<CoachingSession>> = repository.allSessions
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Filters & Search
  val searchQuery = MutableStateFlow("")
  val selectedFeeFilter = MutableStateFlow<FeeType?>(null)
  val selectedMethodFilter = MutableStateFlow<CoachingMethod?>(null)
  val selectedTypeFilter = MutableStateFlow<CoachingType?>(null)

  val filteredSessions: StateFlow<List<CoachingSession>> = combine(
    allSessions,
    searchQuery,
    selectedFeeFilter,
    selectedMethodFilter,
    selectedTypeFilter
  ) { sessions, query, fee, method, type ->
    sessions.filter { session ->
      val matchesQuery = query.isBlank() ||
        session.clientName.contains(query, ignoreCase = true) ||
        session.topic.contains(query, ignoreCase = true) ||
        session.clientContact.contains(query, ignoreCase = true)

      val matchesFee = fee == null || session.feeType == fee
      val matchesMethod = method == null || session.coachingMethod == method
      val matchesType = type == null || session.coachingType == type

      matchesQuery && matchesFee && matchesMethod && matchesType
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // 50 Hours KAC Statistics
  val statistics: StateFlow<KacStatistics> = allSessions.combine(MutableStateFlow(Unit)) { sessions, _ ->
    val totalMinutes = sessions.sumOf { it.durationMinutes }
    val totalHours = totalMinutes / 60f
    val targetHours = 50.0f
    val progressPercent = ((totalHours / targetHours) * 100f).coerceIn(0f, 100f)
    val remainingMinutes = (3000 - totalMinutes).coerceAtLeast(0)
    val remainingHours = (targetHours - totalHours).coerceAtLeast(0f)
    val isCompleted = totalHours >= targetHours

    val paidMinutes = sessions.filter { it.feeType == FeeType.PAID }.sumOf { it.durationMinutes }
    val proBonoMinutes = sessions.filter { it.feeType == FeeType.PRO_BONO }.sumOf { it.durationMinutes }

    val oneOnOneMinutes = sessions.filter { it.coachingType == CoachingType.ONE_ON_ONE }.sumOf { it.durationMinutes }
    val groupMinutes = sessions.filter { it.coachingType == CoachingType.GROUP }.sumOf { it.durationMinutes }

    val faceToFace = sessions.filter { it.coachingMethod == CoachingMethod.FACE_TO_FACE }.sumOf { it.durationMinutes }
    val video = sessions.filter { it.coachingMethod == CoachingMethod.VIDEO }.sumOf { it.durationMinutes }
    val call = sessions.filter { it.coachingMethod == CoachingMethod.CALL }.sumOf { it.durationMinutes }

    val totalSessions = sessions.size
    val uniqueClients = sessions.map { it.clientName.trim() }.filter { it.isNotBlank() }.distinct().size
    val avgDuration = if (totalSessions > 0) totalMinutes / totalSessions else 0

    KacStatistics(
      totalMinutes = totalMinutes,
      totalHours = totalHours,
      targetHours = targetHours,
      progressPercent = progressPercent,
      remainingMinutes = remainingMinutes,
      remainingHours = remainingHours,
      isCompleted = isCompleted,
      paidMinutes = paidMinutes,
      paidHours = paidMinutes / 60f,
      proBonoMinutes = proBonoMinutes,
      proBonoHours = proBonoMinutes / 60f,
      oneOnOneMinutes = oneOnOneMinutes,
      groupMinutes = groupMinutes,
      faceToFaceMinutes = faceToFace,
      videoMinutes = video,
      callMinutes = call,
      totalSessions = totalSessions,
      uniqueClients = uniqueClients,
      averageDurationMinutes = avgDuration
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = KacStatistics()
  )

  // Real-time Timer State
  val timerState = MutableStateFlow(TimerUiState())

  fun setTimerPreset(minutes: Int) {
    if (!timerState.value.isRunning && !timerState.value.isPaused) {
      timerState.value = timerState.value.copy(presetMinutes = minutes)
    }
  }

  fun updateTimerMeta(
    clientName: String = timerState.value.clientName,
    topic: String = timerState.value.topic,
    feeType: FeeType = timerState.value.feeType,
    method: CoachingMethod = timerState.value.coachingMethod,
    type: CoachingType = timerState.value.coachingType
  ) {
    timerState.value = timerState.value.copy(
      clientName = clientName,
      topic = topic,
      feeType = feeType,
      coachingMethod = method,
      coachingType = type
    )
  }

  fun startTimer() {
    val now = System.currentTimeMillis()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
    val formattedStart = if (timerState.value.startTimeFormatted.isBlank()) {
      timeFormat.format(Date(now))
    } else {
      timerState.value.startTimeFormatted
    }

    timerState.value = timerState.value.copy(
      isRunning = true,
      isPaused = false,
      startTimeFormatted = formattedStart,
      startTimestamp = if (timerState.value.startTimestamp == 0L) now else timerState.value.startTimestamp
    )

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      val baseTick = System.currentTimeMillis() - timerState.value.elapsedMillis
      var lastVibratedSec = -1L
      while (isActive && timerState.value.isRunning) {
        delay(40) // ~25-30fps smooth needle & millisecond update
        val currentMillis = (System.currentTimeMillis() - baseTick).coerceAtLeast(0L)
        val currentSec = currentMillis / 1000L

        timerState.value = timerState.value.copy(
          elapsedMillis = currentMillis,
          elapsedSeconds = currentSec
        )

        // Check target time reached vibration once
        val targetSec = timerState.value.presetMinutes * 60L
        if (targetSec > 0 && currentSec >= targetSec && lastVibratedSec != targetSec) {
          lastVibratedSec = targetSec
          triggerHapticAlert()
        }
      }
    }
  }

  fun pauseTimer() {
    timerJob?.cancel()
    timerState.value = timerState.value.copy(
      isRunning = false,
      isPaused = true
    )
  }

  fun stopTimer() {
    timerJob?.cancel()
    timerState.value = timerState.value.copy(
      isRunning = false,
      isPaused = true
    )
  }

  fun recordLap(stageName: String = "") {
    val currentMillis = timerState.value.elapsedMillis
    if (currentMillis <= 0) return
    val currentLaps = timerState.value.laps
    val lastTotal = currentLaps.lastOrNull()?.totalTimeMillis ?: 0L
    val lapDuration = (currentMillis - lastTotal).coerceAtLeast(0L)
    val nextNumber = currentLaps.size + 1
    val defaultStage = when (nextNumber) {
      1 -> "라포 및 코칭 동의"
      2 -> "목표 및 주제 명확화"
      3 -> "현재 상태 탐색 및 통찰"
      4 -> "실행 방안 및 대안 수립"
      5 -> "실행 의지 및 마무리"
      else -> "구간 $nextNumber"
    }

    val newLap = LapItem(
      lapNumber = nextNumber,
      lapTimeMillis = lapDuration,
      totalTimeMillis = currentMillis,
      stageName = stageName.ifBlank { defaultStage }
    )

    timerState.value = timerState.value.copy(
      laps = currentLaps + newLap
    )
  }

  fun resetTimer() {
    timerJob?.cancel()
    timerState.value = TimerUiState(
      presetMinutes = timerState.value.presetMinutes,
      clientName = timerState.value.clientName,
      topic = timerState.value.topic,
      feeType = timerState.value.feeType,
      coachingMethod = timerState.value.coachingMethod,
      coachingType = timerState.value.coachingType,
      laps = emptyList()
    )
  }

  private fun triggerHapticAlert() {
    try {
      val context = getApplication<Application>()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createWaveform(longArrayOf(0, 200, 150, 200), -1)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(300)
        }
      }
    } catch (_: Exception) {
      // Graceful fallback if vibrator unavailable
    }
  }

  fun saveSession(session: CoachingSession) {
    viewModelScope.launch {
      if (session.id == 0L) {
        repository.insertSession(session)
      } else {
        repository.updateSession(session)
      }
    }
  }

  fun deleteSession(session: CoachingSession) {
    viewModelScope.launch {
      repository.deleteSession(session)
    }
  }

  fun deleteSessionById(id: Long) {
    viewModelScope.launch {
      repository.deleteSessionById(id)
    }
  }

  fun resetToSampleData() {
    viewModelScope.launch {
      repository.clearAll()
      repository.insertSessions(AppDatabase.sampleSessions)
    }
  }

  fun clearAllData() {
    viewModelScope.launch {
      repository.clearAll()
    }
  }

  // KAC CSV export content
  fun generateKacCsv(): String {
    val sessions = allSessions.value
    val timeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    val sb = StringBuilder()
    // KAC Standard Table Headers (한국코치협회 코칭일지 표준)
    sb.append("연번,코칭일자,고객명,연락처,코칭형태,인원수,구분(유/무료),코칭방식,시작시간,종료시간,소요시간(분),소요시간(h),회차,코칭주제,코치성찰및합의사항\n")

    sessions.forEachIndexed { index, s ->
      val no = index + 1
      val dateStr = timeFormat.format(Date(s.dateMillis))
      val hours = String.format(Locale.US, "%.1f", s.durationMinutes / 60.0)
      val escapedTopic = escapeCsv(s.topic)
      val escapedReflection = escapeCsv(s.coachReflection)
      val escapedContact = escapeCsv(s.clientContact)
      val escapedName = escapeCsv(s.clientName)

      sb.append("$no,$dateStr,$escapedName,$escapedContact,${s.coachingType.displayName},${s.groupMemberCount},${s.feeType.displayName},${s.coachingMethod.displayName},${s.startTime},${s.endTime},${s.durationMinutes},$hours,${s.sessionRound},$escapedTopic,$escapedReflection\n")
    }
    return sb.toString()
  }

  private fun escapeCsv(value: String): String {
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return "\"${value.replace("\"", "\"\"")}\""
    }
    return value
  }

  // Text summary for KakaoTalk, Notes, Email
  fun generateSummaryReport(): String {
    val stats = statistics.value
    val sessions = allSessions.value
    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    val sb = StringBuilder()
    sb.append("📋 [KAC 한국코치협회] 50시간 실습 코칭기록 요약\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("• 총 누적 코칭 시간: ${stats.totalMinutes / 60}시간 ${stats.totalMinutes % 60}분 (${String.format(Locale.US, "%.1f", stats.totalHours)}h / 50.0h)\n")
    sb.append("• 50시간 달성률: ${String.format(Locale.US, "%.1f", stats.progressPercent)}% (${if (stats.isCompleted) "✅ 50시간 달성 완료!" else "목표까지 ${stats.remainingMinutes / 60}시간 ${stats.remainingMinutes % 60}분 남음"})\n")
    sb.append("• 총 코칭 세션 수: ${stats.totalSessions}회 (총 ${stats.uniqueClients}명의 고객)\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("【세부 분류별 통계】\n")
    sb.append("• 유료 코칭: ${stats.paidMinutes / 60}시간 ${stats.paidMinutes % 60}분 (${String.format(Locale.US, "%.1f", stats.paidHours)}h)\n")
    sb.append("• 무료/프로보노: ${stats.proBonoMinutes / 60}시간 ${stats.proBonoMinutes % 60}분 (${String.format(Locale.US, "%.1f", stats.proBonoHours)}h)\n")
    sb.append("• 1:1 개인 코칭: ${stats.oneOnOneMinutes / 60}h | 그룹 코칭: ${stats.groupMinutes / 60}h\n")
    sb.append("• 코칭 방식: 대면 ${stats.faceToFaceMinutes / 60}h | 화상 ${stats.videoMinutes / 60}h | 전화 ${stats.callMinutes / 60}h\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("【최근 코칭 세션 기록 (${sessions.size}건 중 최근)】\n")

    sessions.take(10).forEachIndexed { i, s ->
      val dateStr = dateFormat.format(Date(s.dateMillis))
      sb.append("${i + 1}. $dateStr | ${s.clientName} | ${s.feeType.displayName} | ${s.coachingMethod.displayName} | ${s.startTime}~${s.endTime} (${s.durationMinutes}분)\n")
      sb.append("   주제: ${s.topic}\n")
    }

    if (sessions.size > 10) {
      sb.append("... 외 ${sessions.size - 10}건의 코칭 세션\n")
    }

    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("KAC 코칭 50시간 기록장 앱에서 생성됨")
    return sb.toString()
  }
}
