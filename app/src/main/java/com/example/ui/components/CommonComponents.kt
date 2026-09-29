package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CoachingMethod
import com.example.model.CoachingType
import com.example.model.FeeType
import com.example.ui.theme.BadgeFree
import com.example.ui.theme.BadgeFreeContainer
import com.example.ui.theme.BadgeMilestone
import com.example.ui.theme.BadgeMilestoneContainer
import com.example.ui.theme.BadgePaid
import com.example.ui.theme.BadgePaidContainer
import com.example.viewmodel.KacStatistics
import java.util.Locale

@Composable
fun FeeTypeBadge(
  feeType: FeeType,
  modifier: Modifier = Modifier
) {
  val isPaid = feeType == FeeType.PAID
  val bgColor = if (isPaid) BadgePaidContainer else BadgeFreeContainer
  val textColor = if (isPaid) BadgePaid else BadgeFree
  val icon = if (isPaid) Icons.Default.WorkspacePremium else Icons.Default.VolunteerActivism

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = bgColor,
    modifier = modifier.testTag("badge_fee_${feeType.name}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = feeType.displayName,
        tint = textColor,
        modifier = Modifier.size(13.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = feeType.displayName,
        color = textColor,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun CoachingMethodBadge(
  method: CoachingMethod,
  modifier: Modifier = Modifier
) {
  val icon = when (method) {
    CoachingMethod.FACE_TO_FACE -> Icons.Default.Person
    CoachingMethod.VIDEO -> Icons.Default.Videocam
    CoachingMethod.CALL -> Icons.Default.Call
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    modifier = modifier.testTag("badge_method_${method.name}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = method.displayName,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = method.displayName,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun CoachingTypeBadge(
  type: CoachingType,
  groupCount: Int = 1,
  modifier: Modifier = Modifier
) {
  val label = if (type == CoachingType.GROUP) "그룹(${groupCount}인)" else "1:1"
  val icon = if (type == CoachingType.GROUP) Icons.Default.Group else Icons.Default.Person

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
    modifier = modifier.testTag("badge_type_${type.name}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = label,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 11.sp
      )
    }
  }
}

@Composable
fun KacGoalProgressHeader(
  stats: KacStatistics,
  onCardClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
    ),
    shape = RoundedCornerShape(16.dp),
    modifier = modifier
      .fillMaxWidth()
      .clickable { onCardClick() }
      .testTag("kac_goal_progress_card")
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(
                if (stats.isCompleted) BadgePaid else MaterialTheme.colorScheme.primary
              )
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "KAC 50시간 실습 목표",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (stats.isCompleted) BadgePaidContainer else BadgeMilestoneContainer
        ) {
          Text(
            text = if (stats.isCompleted) "🎉 50h 달성 완료" else "남은 시간: ${stats.remainingMinutes / 60}h ${stats.remainingMinutes % 60}m",
            color = if (stats.isCompleted) BadgePaid else BadgeMilestone,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "${stats.totalMinutes / 60}",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "시간 ${stats.totalMinutes % 60}분",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = " / 50시간",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
              modifier = Modifier.padding(start = 4.dp)
            )
          }
          Text(
            text = "유료 ${String.format(Locale.US, "%.1f", stats.paidHours)}h · 무료 ${String.format(Locale.US, "%.1f", stats.proBonoHours)}h · 총 ${stats.totalSessions}회",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }

        Text(
          text = "${String.format(Locale.US, "%.1f", stats.progressPercent)}%",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      LinearProgressIndicator(
        progress = { stats.progressPercent / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round,
      )
    }
  }
}
