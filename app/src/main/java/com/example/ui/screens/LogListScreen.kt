package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CoachingMethod
import com.example.model.CoachingSession
import com.example.model.CoachingType
import com.example.model.FeeType
import com.example.ui.components.CoachingMethodBadge
import com.example.ui.components.CoachingTypeBadge
import com.example.ui.components.FeeTypeBadge
import com.example.viewmodel.CoachingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogListScreen(
  viewModel: CoachingViewModel,
  onAddNewSession: () -> Unit,
  onEditSession: (CoachingSession) -> Unit,
  modifier: Modifier = Modifier
) {
  val sessions by viewModel.filteredSessions.collectAsStateWithLifecycle()
  val query by viewModel.searchQuery.collectAsStateWithLifecycle()
  val feeFilter by viewModel.selectedFeeFilter.collectAsStateWithLifecycle()
  val methodFilter by viewModel.selectedMethodFilter.collectAsStateWithLifecycle()
  val typeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()

  var sessionToDelete by remember { mutableStateOf<CoachingSession?>(null) }

  val totalMinutesInList = sessions.sumOf { it.durationMinutes }

  Box(modifier = modifier.fillMaxSize().testTag("log_list_screen")) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Search Bar
      OutlinedTextField(
        value = query,
        onValueChange = { viewModel.searchQuery.value = it },
        placeholder = { Text("고객명, 코칭 주제, 연락처 검색") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "검색") },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "지우기")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_text_field")
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Filter Chips Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Fee Filters
        FilterChip(
          selected = feeFilter == null,
          onClick = { viewModel.selectedFeeFilter.value = null },
          label = { Text("전체 구분") },
          modifier = Modifier.testTag("filter_fee_all")
        )
        FeeType.entries.forEach { f ->
          FilterChip(
            selected = feeFilter == f,
            onClick = {
              viewModel.selectedFeeFilter.value = if (feeFilter == f) null else f
            },
            label = { Text(f.displayName) },
            modifier = Modifier.testTag("filter_fee_${f.name}")
          )
        }

        // Type Filters
        CoachingType.entries.forEach { t ->
          FilterChip(
            selected = typeFilter == t,
            onClick = {
              viewModel.selectedTypeFilter.value = if (typeFilter == t) null else t
            },
            label = { Text(t.displayName) },
            modifier = Modifier.testTag("filter_type_${t.name}")
          )
        }

        // Method Filters
        CoachingMethod.entries.forEach { m ->
          FilterChip(
            selected = methodFilter == m,
            onClick = {
              viewModel.selectedMethodFilter.value = if (methodFilter == m) null else m
            },
            label = { Text(m.displayName) },
            modifier = Modifier.testTag("filter_method_${m.name}")
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // List Count Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "기록된 코칭일지 (${sessions.size}건)",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "합계: ${totalMinutesInList / 60}시간 ${totalMinutesInList % 60}분 (${String.format(Locale.US, "%.1f", totalMinutesInList / 60.0)}h)",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      if (sessions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.MenuBook,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (query.isNotBlank() || feeFilter != null) "검색 조건에 맞는 일지가 없습니다" else "아직 등록된 코칭일지가 없습니다",
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "타임로그 타이머로 측정하거나 수기로 일지를 작성해보세요.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .testTag("coaching_sessions_list"),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(sessions, key = { it.id }) { session ->
            CoachingSessionCard(
              session = session,
              onEdit = { onEditSession(session) },
              onDelete = { sessionToDelete = session }
            )
          }
          item {
            Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB
          }
        }
      }
    }

    // Extended Floating Action Button: Add new log manually
    ExtendedFloatingActionButton(
      onClick = onAddNewSession,
      icon = { Icon(Icons.Default.Add, contentDescription = null) },
      text = { Text("수기 일지 작성") },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 16.dp, bottom = 16.dp)
        .testTag("fab_add_session")
    )
  }

  // Delete Confirmation Dialog
  sessionToDelete?.let { session ->
    AlertDialog(
      onDismissRequest = { sessionToDelete = null },
      title = { Text("코칭일지 삭제") },
      text = {
        Text("고객 '${session.clientName}' 님의 ${session.durationMinutes}분 코칭 기록을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.")
      },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.deleteSession(session)
            sessionToDelete = null
          },
          modifier = Modifier.testTag("dialog_confirm_delete")
        ) {
          Text("삭제", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { sessionToDelete = null }) {
          Text("취소")
        }
      }
    )
  }
}

@Composable
fun CoachingSessionCard(
  session: CoachingSession,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }
  val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { isExpanded = !isExpanded }
      .testTag("session_item_${session.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Date, Round, Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = dateFormat.format(Date(session.dateMillis)),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "${session.sessionRound}회차",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          FeeTypeBadge(feeType = session.feeType)
          CoachingMethodBadge(method = session.coachingMethod)
          CoachingTypeBadge(type = session.coachingType, groupCount = session.groupMemberCount)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Client and Topic
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = session.clientName,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${session.startTime} ~ ${session.endTime} (${session.durationMinutes}분)",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      if (session.clientContact.isNotBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = session.clientContact,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "주제: ${session.topic}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )

      // Expandable section for reflection and actions
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
          Spacer(modifier = Modifier.height(8.dp))

          if (session.coachReflection.isNotBlank()) {
            Text(
              text = "코치 성찰 및 합의사항:",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = session.coachReflection,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          // Card Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            IconButton(
              onClick = onEdit,
              modifier = Modifier.size(36.dp).testTag("btn_edit_${session.id}")
            ) {
              Icon(
                Icons.Default.Edit,
                contentDescription = "수정",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
              onClick = onDelete,
              modifier = Modifier.size(36.dp).testTag("btn_delete_${session.id}")
            ) {
              Icon(
                Icons.Default.DeleteOutline,
                contentDescription = "삭제",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // Chevron icon hint
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = if (isExpanded) "접기" else "펼치기",
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
