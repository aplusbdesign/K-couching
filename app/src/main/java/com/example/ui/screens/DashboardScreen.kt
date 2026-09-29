package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BadgeFree
import com.example.ui.theme.BadgeFreeContainer
import com.example.ui.theme.BadgeMilestone
import com.example.ui.theme.BadgeMilestoneContainer
import com.example.ui.theme.BadgePaid
import com.example.ui.theme.BadgePaidContainer
import com.example.viewmodel.CoachingViewModel
import java.util.Locale

@Composable
fun DashboardScreen(
  viewModel: CoachingViewModel,
  modifier: Modifier = Modifier
) {
  val stats by viewModel.statistics.collectAsStateWithLifecycle()
  val sessions by viewModel.allSessions.collectAsStateWithLifecycle()

  // Group by client
  val clientStats = sessions
    .groupBy { it.clientName.trim() }
    .filter { it.key.isNotBlank() }
    .map { (client, list) ->
      Triple(
        client,
        list.size,
        list.sumOf { it.durationMinutes }
      )
    }
    .sortedByDescending { it.third }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("dashboard_screen")
  ) {
    // Top Hero Milestone Card
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("milestone_hero_card")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "KAC 코칭 50시간 실습 달성도",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          if (stats.isCompleted) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = BadgePaidContainer
            ) {
              Text(
                text = "인증 기준 달성!",
                color = BadgePaid,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big Progress Gauge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(180.dp)
        ) {
          CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            strokeWidth = 14.dp,
            strokeCap = StrokeCap.Round
          )
          CircularProgressIndicator(
            progress = { stats.progressPercent / 100f },
            modifier = Modifier.fillMaxSize(),
            color = if (stats.isCompleted) BadgePaid else MaterialTheme.colorScheme.primary,
            strokeWidth = 14.dp,
            strokeCap = StrokeCap.Round
          )
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "${String.format(Locale.US, "%.1f", stats.progressPercent)}%",
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "${stats.totalMinutes / 60}h ${stats.totalMinutes % 60}m / 50h",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = if (stats.isCompleted) "50시간 달성 완료!" else "남은 시간: ${stats.remainingMinutes / 60}h ${stats.remainingMinutes % 60}m",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Milestones 10h, 25h, 35h, 50h
        MilestoneStepBar(currentHours = stats.totalHours)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Key Stats Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      StatMetricCard(
        title = "유료 코칭",
        value = "${stats.paidMinutes / 60}h ${stats.paidMinutes % 60}m",
        subtext = "${String.format(Locale.US, "%.1f", stats.paidHours)}시간",
        icon = Icons.Default.WorkspacePremium,
        iconTint = BadgePaid,
        bgColor = BadgePaidContainer.copy(alpha = 0.5f),
        modifier = Modifier.weight(1f)
      )
      StatMetricCard(
        title = "무료/프로보노",
        value = "${stats.proBonoMinutes / 60}h ${stats.proBonoMinutes % 60}m",
        subtext = "${String.format(Locale.US, "%.1f", stats.proBonoHours)}시간",
        icon = Icons.Default.VolunteerActivism,
        iconTint = BadgeFree,
        bgColor = BadgeFreeContainer.copy(alpha = 0.5f),
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      StatMetricCard(
        title = "총 세션 수",
        value = "${stats.totalSessions}회",
        subtext = "평균 ${stats.averageDurationMinutes}분/세션",
        icon = Icons.Default.Schedule,
        iconTint = MaterialTheme.colorScheme.primary,
        bgColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.weight(1f)
      )
      StatMetricCard(
        title = "고객 수",
        value = "${stats.uniqueClients}명",
        subtext = "누적 코칭 고객",
        icon = Icons.Default.Person,
        iconTint = MaterialTheme.colorScheme.secondary,
        bgColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Detailed Breakdown Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "코칭 세부 분류 현황",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Paid vs Free Ratio
        val total = stats.totalMinutes.coerceAtLeast(1)
        val paidRatio = (stats.paidMinutes.toFloat() / total).coerceIn(0f, 1f)
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "유료 vs 무료 비율",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Medium
            )
            Text(
              text = "유료 ${(paidRatio * 100).toInt()}% / 무료 ${(100 - (paidRatio * 100).toInt())}%",
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = BadgePaid
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          LinearProgressIndicator(
            progress = { paidRatio },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = BadgePaid,
            trackColor = BadgeFree,
            strokeCap = StrokeCap.Round
          )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(14.dp))

        // Format: 1:1 vs Group
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "1:1 개인 코칭", style = MaterialTheme.typography.bodyMedium)
          }
          Text(
            text = "${stats.oneOnOneMinutes / 60}시간 ${stats.oneOnOneMinutes % 60}분",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Group, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "그룹 코칭", style = MaterialTheme.typography.bodyMedium)
          }
          Text(
            text = "${stats.groupMinutes / 60}시간 ${stats.groupMinutes % 60}분",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
          )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(14.dp))

        // Coaching Method Breakdown
        Text(
          text = "코칭 방식별 시간",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          MethodColumn(title = "대면 코칭", minutes = stats.faceToFaceMinutes)
          MethodColumn(title = "화상 코칭", minutes = stats.videoMinutes)
          MethodColumn(title = "전화 코칭", minutes = stats.callMinutes)
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Top Clients Ranking
    if (clientStats.isNotEmpty()) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "고객별 누적 코칭 시간",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(10.dp))

          clientStats.take(6).forEachIndexed { index, (client, count, minutes) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = if (index == 0) BadgeMilestoneContainer else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier.size(24.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${index + 1}",
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = if (index == 0) BadgeMilestone else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = client,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = "${count}회 진행",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Text(
                text = "${minutes / 60}h ${minutes % 60}m",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}

@Composable
fun MilestoneStepBar(currentHours: Float) {
  val milestones = listOf(10, 25, 35, 50)

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    milestones.forEach { target ->
      val achieved = currentHours >= target
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(
              if (achieved) BadgePaid else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
        ) {
          if (achieved) {
            Icon(
              Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.surface,
              modifier = Modifier.size(18.dp)
            )
          } else {
            Text(
              text = "${target}h",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (target == 50) "KAC완료" else if (target == 35) "유료권장" else "${target}h",
          style = MaterialTheme.typography.labelSmall,
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
      }
    }
  }
}

@Composable
fun MethodColumn(title: String, minutes: Int) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = "${minutes / 60}h ${minutes % 60}m",
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
fun StatMetricCard(
  title: String,
  value: String,
  subtext: String,
  icon: ImageVector,
  iconTint: androidx.compose.ui.graphics.Color,
  bgColor: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = bgColor,
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtext,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
