package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CoachingMethod
import com.example.model.CoachingType
import com.example.model.FeeType
import com.example.ui.components.KacGoalProgressHeader
import com.example.ui.components.PhotorealisticStopwatch
import com.example.ui.dialogs.PrefillTimerData
import com.example.viewmodel.CoachingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimerScreen(
  viewModel: CoachingViewModel,
  onOpenCompleteDialog: (PrefillTimerData) -> Unit,
  onNavigateToStats: () -> Unit,
  modifier: Modifier = Modifier
) {
  val timerState by viewModel.timerState.collectAsStateWithLifecycle()
  val stats by viewModel.statistics.collectAsStateWithLifecycle()
  val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()

  // Recent client names for quick selection
  val recentClients = allSessions
    .map { it.clientName.trim() }
    .filter { it.isNotBlank() }
    .distinct()
    .take(5)

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("timer_screen")
  ) {
    // 1. 50 Hours KAC Goal Progress Summary Header
    KacGoalProgressHeader(
      stats = stats,
      onCardClick = onNavigateToStats
    )

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Photorealistic Mechanical Stopwatch Card
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("timer_card")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Preset Target Duration Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "세션 목표 시간:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(30, 45, 50, 60, 0).forEach { mins ->
              val label = if (mins == 0) "자유" else "${mins}분"
              val isSelected = timerState.presetMinutes == mins
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clickable(enabled = !timerState.isRunning && !timerState.isPaused) {
                    viewModel.setTimerPreset(mins)
                  }
                  .testTag("preset_button_${mins}")
              ) {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- PHOTOREALISTIC STOPWATCH WITH CONVEX DOMED GLASS ---
        PhotorealisticStopwatch(
          elapsedMillis = timerState.elapsedMillis,
          isRunning = timerState.isRunning,
          targetPresetMinutes = timerState.presetMinutes,
          onTopPusherClick = {
            if (timerState.isRunning) {
              viewModel.pauseTimer()
            } else {
              viewModel.startTimer()
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- REAL-TIME STOPWATCH CONTROLS: START, PAUSE, STOP, LAP, RESET ---
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (!timerState.isRunning && !timerState.isPaused) {
            // State: Ready (Initial)
            // Primary START Button
            Button(
              onClick = { viewModel.startTimer() },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
              ),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("timer_start_button")
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("스톱와치 시작", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            // State: Running or Paused
            // 1. Reset Button (Enabled when paused)
            OutlinedButton(
              onClick = { viewModel.resetTimer() },
              enabled = !timerState.isRunning,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .height(48.dp)
                .testTag("timer_reset_button")
            ) {
              Icon(Icons.Default.Refresh, contentDescription = "초기화", modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("초기화", fontSize = 13.sp)
            }

            // 2. Lap / Checkpoint Button (Enabled when running)
            if (timerState.isRunning) {
              OutlinedButton(
                onClick = { viewModel.recordLap() },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .height(48.dp)
                  .testTag("timer_lap_button")
              ) {
                Icon(Icons.Default.Flag, contentDescription = "구간 기록", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("구간", fontSize = 13.sp)
              }
            }

            // 3. START / PAUSE Button
            if (timerState.isRunning) {
              Button(
                onClick = { viewModel.pauseTimer() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.tertiary
                ),
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("timer_pause_button")
              ) {
                Icon(Icons.Default.Pause, contentDescription = "일시정지", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("일시정지", fontWeight = FontWeight.Bold)
              }
            } else {
              Button(
                onClick = { viewModel.startTimer() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("timer_resume_button")
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "재개", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("계속 진행", fontWeight = FontWeight.Bold)
              }
            }

            // 4. STOP & FINISH SESSION BUTTON (Prominent red/secondary stop action)
            Button(
              onClick = {
                val now = System.currentTimeMillis()
                val elapsedSec = timerState.elapsedSeconds
                val durationMins = ((elapsedSec + 59) / 60).toInt().coerceAtLeast(1)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
                val endTime = timeFormat.format(Date(now))
                val startTime = if (timerState.startTimeFormatted.isNotBlank()) {
                  timerState.startTimeFormatted
                } else {
                  timeFormat.format(Date(now - (elapsedSec * 1000L)))
                }

                viewModel.stopTimer()

                onOpenCompleteDialog(
                  PrefillTimerData(
                    clientName = timerState.clientName,
                    topic = timerState.topic,
                    feeType = timerState.feeType,
                    coachingMethod = timerState.coachingMethod,
                    coachingType = timerState.coachingType,
                    startTime = startTime,
                    endTime = endTime,
                    durationMinutes = durationMins,
                    dateMillis = timerState.startTimestamp.takeIf { it > 0 } ?: now
                  )
                )
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
              ),
              modifier = Modifier
                .height(48.dp)
                .testTag("timer_stop_button")
            ) {
              Icon(Icons.Default.Stop, contentDescription = "종료", modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("세션 종료", fontWeight = FontWeight.Bold)
            }
          }
        }

        // Dedicated Full Stop / Save Action Banner when elapsed
        AnimatedVisibility(
          visible = timerState.elapsedSeconds > 0,
          enter = fadeIn() + expandVertically(),
          exit = fadeOut() + shrinkVertically()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 12.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  val now = System.currentTimeMillis()
                  val elapsedSec = timerState.elapsedSeconds
                  val durationMins = ((elapsedSec + 59) / 60).toInt().coerceAtLeast(1)
                  val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
                  val endTime = timeFormat.format(Date(now))
                  val startTime = if (timerState.startTimeFormatted.isNotBlank()) {
                    timerState.startTimeFormatted
                  } else {
                    timeFormat.format(Date(now - (elapsedSec * 1000L)))
                  }

                  viewModel.stopTimer()

                  onOpenCompleteDialog(
                    PrefillTimerData(
                      clientName = timerState.clientName,
                      topic = timerState.topic,
                      feeType = timerState.feeType,
                      coachingMethod = timerState.coachingMethod,
                      coachingType = timerState.coachingType,
                      startTime = startTime,
                      endTime = endTime,
                      durationMinutes = durationMins,
                      dateMillis = timerState.startTimestamp.takeIf { it > 0 } ?: now
                    )
                  )
                }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "측정 완료 및 코칭일지에 반영",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                  )
                }
                Text(
                  text = "${((timerState.elapsedSeconds + 59) / 60)}분 인정",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }
    }

    // 3. Lap / Coaching Stage Checkpoints (구간 기록 목록)
    AnimatedVisibility(visible = timerState.laps.isNotEmpty()) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 14.dp)
          .testTag("laps_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "코칭 단계별 체크포인트 (${timerState.laps.size}건)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "구간 시간 / 누적",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          timerState.laps.reversed().forEachIndexed { index, lap ->
            val lapMins = (lap.lapTimeMillis / 1000) / 60
            val lapSecs = (lap.lapTimeMillis / 1000) % 60
            val totMins = (lap.totalTimeMillis / 1000) / 60
            val totSecs = (lap.totalTimeMillis / 1000) % 60

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Text(
                    text = "#${lap.lapNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = lap.stageName,
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Medium
                )
              }

              Text(
                text = String.format(Locale.US, "+%02d:%02d (%02d:%02d)", lapMins, lapSecs, totMins, totSecs),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            if (index < timerState.laps.size - 1) {
              HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 4. Pre-session Metadata Card (고객명, 코칭 주제, 유/무료 구분)
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("timer_metadata_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "세션 기본 정보 (선택 입력)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = "측정 중 미리 입력해두면 세션 종료 시 코칭일지에 자동 반영됩니다.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = timerState.clientName,
          onValueChange = { viewModel.updateTimerMeta(clientName = it) },
          label = { Text("고객 성명") },
          placeholder = { Text("예: 박수진") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("timer_input_client")
        )

        // Recent clients fast picker
        if (recentClients.isNotEmpty() && timerState.clientName.isBlank()) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "최근 고객 바로 선택:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            recentClients.forEach { name ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable {
                  viewModel.updateTimerMeta(clientName = name)
                }
              ) {
                Text(
                  text = name,
                  style = MaterialTheme.typography.labelSmall,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = timerState.topic,
          onValueChange = { viewModel.updateTimerMeta(topic = it) },
          label = { Text("코칭 주제 / 목표") },
          placeholder = { Text("예: 2분기 성과 피드백 및 목표 정렬") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("timer_input_topic")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Fee Type Selection
        Text(
          text = "구분 (KAC 필수)",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          FeeType.entries.forEach { f ->
            FilterChip(
              selected = timerState.feeType == f,
              onClick = { viewModel.updateTimerMeta(feeType = f) },
              label = { Text(f.displayName) },
              modifier = Modifier.testTag("timer_fee_${f.name}")
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Method Selection
        Text(
          text = "코칭 방식",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          CoachingMethod.entries.forEach { m ->
            FilterChip(
              selected = timerState.coachingMethod == m,
              onClick = { viewModel.updateTimerMeta(method = m) },
              label = { Text(m.displayName) },
              modifier = Modifier.testTag("timer_method_${m.name}")
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
