package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CoachingSession
import com.example.viewmodel.CoachingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportReportScreen(
  viewModel: CoachingViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
  val stats by viewModel.statistics.collectAsStateWithLifecycle()

  var showResetConfirm by remember { mutableStateOf(false) }
  var showClearConfirm by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("export_report_screen")
  ) {
    // KAC Guidelines Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "한국코치협회(KAC) 코칭일지 제출 규정",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "• KAC 인증 시험 응시를 위해 총 50시간 이상의 코칭 실습 기록이 필수적입니다.\n" +
            "• 유료 코칭과 무료/프로보노 코칭을 명확하게 구분하여 기록해야 합니다.\n" +
            "• 1:1 개인 코칭뿐만 아니라 그룹 코칭(참석 인원수 명시)도 인정 기준에 맞춰 합산됩니다.\n" +
            "• 협회 양식(Excel/CSV)에 맞추어 연번, 일자, 고객 정보, 방식, 소요시간, 성찰 사항을 기재하여 제출합니다.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 20.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 1-Click Export CSV Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "1. KAC 표준 코칭일지 CSV 내보내기",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "엑셀(Excel), 구글 스프레드시트에서 바로 열 수 있는 표준 CSV 형식입니다.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              val csvContent = viewModel.generateKacCsv()
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csvContent)
                putExtra(Intent.EXTRA_SUBJECT, "KAC 코칭 50시간 실습 코칭일지 (한국코치협회 제출용)")
                type = "text/plain"
              }
              val shareIntent = Intent.createChooser(sendIntent, "코칭일지 CSV 파일 공유/저장")
              context.startActivity(shareIntent)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("btn_share_csv")
          ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("CSV 파일 공유")
          }

          OutlinedButton(
            onClick = {
              val csvContent = viewModel.generateKacCsv()
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("KAC 코칭일지 CSV", csvContent)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "CSV 내용이 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("btn_copy_csv")
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("CSV 복사")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 1-Click Text Report Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "2. 실습 현황 요약 리포트",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "코치 멘토링, 스터디, SNS 또는 메모장에 바로 붙여넣을 수 있는 텍스트 요약입니다.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              val report = viewModel.generateSummaryReport()
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, report)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "코칭 실습 요약 리포트 공유"))
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("btn_share_summary")
          ) {
            Icon(Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("요약본 공유")
          }

          OutlinedButton(
            onClick = {
              val report = viewModel.generateSummaryReport()
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("KAC 실습 요약 리포트", report)
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "실습 요약이 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("btn_copy_summary")
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("요약본 복사")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Form Preview Table Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.TableChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "한국코치협회 제출 서식 미리보기",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = "가로로 스크롤하여 제출 양식의 각 컬럼을 확인하세요.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Table
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .horizontalScroll(rememberScrollState())
              .padding(8.dp)
          ) {
            // Header Row
            Row(
              modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp)).padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              TableCell(text = "연번", width = 45.dp, isHeader = true)
              TableCell(text = "일자", width = 85.dp, isHeader = true)
              TableCell(text = "고객명", width = 70.dp, isHeader = true)
              TableCell(text = "연락처", width = 110.dp, isHeader = true)
              TableCell(text = "형태", width = 70.dp, isHeader = true)
              TableCell(text = "구분", width = 65.dp, isHeader = true)
              TableCell(text = "방식", width = 55.dp, isHeader = true)
              TableCell(text = "시간", width = 100.dp, isHeader = true)
              TableCell(text = "소요", width = 55.dp, isHeader = true)
              TableCell(text = "주제", width = 180.dp, isHeader = true)
            }

            // Data Rows
            val dateFormat = SimpleDateFormat("MM.dd", Locale.KOREA)
            sessions.take(15).forEachIndexed { i, s ->
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                TableCell(text = "${i + 1}", width = 45.dp)
                TableCell(text = dateFormat.format(Date(s.dateMillis)), width = 85.dp)
                TableCell(text = s.clientName, width = 70.dp)
                TableCell(text = s.clientContact.ifBlank { "-" }, width = 110.dp)
                TableCell(text = s.coachingType.displayName, width = 70.dp)
                TableCell(text = s.feeType.displayName, width = 65.dp)
                TableCell(text = s.coachingMethod.displayName, width = 55.dp)
                TableCell(text = "${s.startTime}~${s.endTime}", width = 100.dp)
                TableCell(text = "${s.durationMinutes}분", width = 55.dp)
                TableCell(text = s.topic, width = 180.dp)
              }
              HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Data Management Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "데이터 관리",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = { showResetConfirm = true },
            modifier = Modifier.weight(1f).testTag("btn_reset_sample")
          ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("샘플 데이터 복원", fontSize = 13.sp)
          }

          OutlinedButton(
            onClick = { showClearConfirm = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.weight(1f).testTag("btn_clear_all")
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("전체 기록 삭제", fontSize = 13.sp)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }

  // Reset Sample Data Dialog
  if (showResetConfirm) {
    AlertDialog(
      onDismissRequest = { showResetConfirm = false },
      title = { Text("샘플 데이터 복원") },
      text = { Text("기본 코칭 실습 샘플 데이터(3건)로 초기화하시겠습니까?") },
      confirmButton = {
        TextButton(onClick = {
          viewModel.resetToSampleData()
          showResetConfirm = false
          Toast.makeText(context, "샘플 데이터가 복원되었습니다.", Toast.LENGTH_SHORT).show()
        }) {
          Text("복원")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirm = false }) {
          Text("취소")
        }
      }
    )
  }

  // Clear All Data Dialog
  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("전체 기록 삭제") },
      text = { Text("저장된 모든 코칭일지를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
      confirmButton = {
        TextButton(onClick = {
          viewModel.clearAllData()
          showClearConfirm = false
          Toast.makeText(context, "모든 기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
        }) {
          Text("전체 삭제", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirm = false }) {
          Text("취소")
        }
      }
    )
  }
}

@Composable
fun TableCell(
  text: String,
  width: androidx.compose.ui.unit.Dp,
  isHeader: Boolean = false
) {
  Text(
    text = text,
    style = if (isHeader) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
    fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
    color = if (isHeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
    modifier = Modifier.width(width)
  )
}
