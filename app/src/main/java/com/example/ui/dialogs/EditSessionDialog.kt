package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CoachingMethod
import com.example.model.CoachingSession
import com.example.model.CoachingType
import com.example.model.FeeType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditSessionDialog(
  initialSession: CoachingSession? = null,
  prefillFromTimer: PrefillTimerData? = null,
  onDismiss: () -> Unit,
  onSave: (CoachingSession) -> Unit
) {
  val isEdit = initialSession != null

  var clientName by remember {
    mutableStateOf(initialSession?.clientName ?: prefillFromTimer?.clientName ?: "")
  }
  var clientContact by remember {
    mutableStateOf(initialSession?.clientContact ?: "")
  }
  var coachingType by remember {
    mutableStateOf(initialSession?.coachingType ?: prefillFromTimer?.coachingType ?: CoachingType.ONE_ON_ONE)
  }
  var groupMemberCount by remember {
    mutableIntStateOf(initialSession?.groupMemberCount ?: 1)
  }
  var feeType by remember {
    mutableStateOf(initialSession?.feeType ?: prefillFromTimer?.feeType ?: FeeType.PAID)
  }
  var coachingMethod by remember {
    mutableStateOf(initialSession?.coachingMethod ?: prefillFromTimer?.coachingMethod ?: CoachingMethod.FACE_TO_FACE)
  }

  val now = System.currentTimeMillis()
  var dateMillis by remember {
    mutableLongStateOf(initialSession?.dateMillis ?: prefillFromTimer?.dateMillis ?: now)
  }
  var startTime by remember {
    mutableStateOf(
      initialSession?.startTime
        ?: prefillFromTimer?.startTime
        ?: SimpleDateFormat("HH:mm", Locale.KOREA).format(Date(now - 3600000))
    )
  }
  var endTime by remember {
    mutableStateOf(
      initialSession?.endTime
        ?: prefillFromTimer?.endTime
        ?: SimpleDateFormat("HH:mm", Locale.KOREA).format(Date(now))
    )
  }
  var durationMinutes by remember {
    mutableIntStateOf(
      initialSession?.durationMinutes ?: prefillFromTimer?.durationMinutes ?: 60
    )
  }
  var sessionRound by remember {
    mutableIntStateOf(initialSession?.sessionRound ?: 1)
  }
  var topic by remember {
    mutableStateOf(initialSession?.topic ?: prefillFromTimer?.topic ?: "")
  }
  var coachReflection by remember {
    mutableStateOf(initialSession?.coachReflection ?: "")
  }

  var nameError by remember { mutableStateOf(false) }
  var topicError by remember { mutableStateOf(false) }

  val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .heightIn(max = 700.dp)
        .testTag("edit_session_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Dialog Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isEdit) "코칭일지 수정" else "코칭일지 기록",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("dialog_close_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "닫기")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Client Info
        Text(
          text = "1. 고객 정보 (필수)",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = clientName,
          onValueChange = {
            clientName = it
            if (it.isNotBlank()) nameError = false
          },
          label = { Text("고객 성명 *") },
          placeholder = { Text("예: 홍길동") },
          isError = nameError,
          supportingText = { if (nameError) Text("고객명을 입력해주세요") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_name")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = clientContact,
          onValueChange = { clientContact = it },
          label = { Text("연락처 또는 소속") },
          placeholder = { Text("010-0000-0000 또는 소속 회사") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_client_contact")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. KAC Type & Fee
        Text(
          text = "2. 코칭 분류 및 유·무료 구분 (KAC 표준)",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Fee Type Selection
        Text(
          text = "구분 (유료 코칭 필수 증빙)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FeeType.entries.forEach { f ->
            FilterChip(
              selected = feeType == f,
              onClick = { feeType = f },
              label = { Text(f.displayName) },
              modifier = Modifier.testTag("chip_fee_${f.name}")
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Coaching Format Selection
        Text(
          text = "코칭 형태",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CoachingType.entries.forEach { t ->
            FilterChip(
              selected = coachingType == t,
              onClick = { coachingType = t },
              label = { Text(t.displayName) },
              modifier = Modifier.testTag("chip_type_${t.name}")
            )
          }
        }

        if (coachingType == CoachingType.GROUP) {
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = groupMemberCount.toString(),
            onValueChange = { groupMemberCount = it.toIntOrNull() ?: 1 },
            label = { Text("참여 인원수") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_group_count")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Method Selection
        Text(
          text = "코칭 방식",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CoachingMethod.entries.forEach { m ->
            FilterChip(
              selected = coachingMethod == m,
              onClick = { coachingMethod = m },
              label = { Text(m.displayName) },
              modifier = Modifier.testTag("chip_method_${m.name}")
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Date & Time
        Text(
          text = "3. 코칭 일시 및 시간",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Date selector summary
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "코칭 일자: ${dateFormat.format(Date(dateMillis))}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("시작 (HH:mm)") },
            placeholder = { Text("14:00") },
            modifier = Modifier
              .weight(1f)
              .testTag("input_start_time")
          )
          OutlinedTextField(
            value = endTime,
            onValueChange = { endTime = it },
            label = { Text("종료 (HH:mm)") },
            placeholder = { Text("15:00") },
            modifier = Modifier
              .weight(1f)
              .testTag("input_end_time")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = durationMinutes.toString(),
            onValueChange = { durationMinutes = it.toIntOrNull() ?: 0 },
            label = { Text("소요 시간 (분 단위) *") },
            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(1f)
              .testTag("input_duration_minutes")
          )
          OutlinedTextField(
            value = sessionRound.toString(),
            onValueChange = { sessionRound = it.toIntOrNull() ?: 1 },
            label = { Text("회차") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
              .weight(0.7f)
              .testTag("input_session_round")
          )
        }

        // Quick duration presets
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf(30, 45, 50, 60, 90, 120).forEach { mins ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (durationMinutes == mins) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { durationMinutes = mins }
                .padding(vertical = 2.dp)
            ) {
              Text(
                text = "${mins}분",
                style = MaterialTheme.typography.labelSmall,
                color = if (durationMinutes == mins) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Topic & Reflection
        Text(
          text = "4. 코칭 주제 및 코치 성찰",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = topic,
          onValueChange = {
            topic = it
            if (it.isNotBlank()) topicError = false
          },
          label = { Text("코칭 주제 및 목표 *") },
          placeholder = { Text("예: 커리어 전환 목표 설정 및 실행 계획") },
          isError = topicError,
          supportingText = { if (topicError) Text("주제를 입력해주세요") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_coaching_topic")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = coachReflection,
          onValueChange = { coachReflection = it },
          label = { Text("코치 성찰 및 다음 회차 합의사항") },
          placeholder = { Text("예: 고객의 진정한 동기를 파악함. 다음 회차까지 행동 과제 1가지 실천 약속.") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_coach_reflection")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("dialog_cancel_button")
          ) {
            Text("취소")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (clientName.isBlank()) {
                nameError = true
                return@Button
              }
              if (topic.isBlank()) {
                topicError = true
                return@Button
              }

              val session = CoachingSession(
                id = initialSession?.id ?: 0L,
                clientName = clientName.trim(),
                clientContact = clientContact.trim(),
                coachingType = coachingType,
                groupMemberCount = if (coachingType == CoachingType.GROUP) groupMemberCount else 1,
                feeType = feeType,
                coachingMethod = coachingMethod,
                dateMillis = dateMillis,
                startTime = startTime.ifBlank { "14:00" },
                endTime = endTime.ifBlank { "15:00" },
                durationMinutes = durationMinutes.coerceAtLeast(1),
                sessionRound = sessionRound.coerceAtLeast(1),
                topic = topic.trim(),
                coachReflection = coachReflection.trim(),
                createdAt = initialSession?.createdAt ?: System.currentTimeMillis()
              )
              onSave(session)
            },
            modifier = Modifier.testTag("dialog_save_button")
          ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isEdit) "수정 완료" else "기록 저장")
          }
        }
      }
    }
  }
}

data class PrefillTimerData(
  val clientName: String,
  val topic: String,
  val feeType: FeeType,
  val coachingMethod: CoachingMethod,
  val coachingType: CoachingType,
  val startTime: String,
  val endTime: String,
  val durationMinutes: Int,
  val dateMillis: Long
)
