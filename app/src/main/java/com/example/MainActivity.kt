package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CoachingSession
import com.example.ui.dialogs.EditSessionDialog
import com.example.ui.dialogs.PrefillTimerData
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExportReportScreen
import com.example.ui.screens.LogListScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.theme.BadgePaid
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CoachingViewModel
import java.util.Locale

enum class AppTab(val label: String, val icon: ImageVector) {
  TIMER("타임로그", Icons.Default.Timer),
  LOGS("코칭일지", Icons.Default.MenuBook),
  DASHBOARD("50h 현황", Icons.Default.Insights),
  EXPORT("제출·공유", Icons.Default.FileDownload)
}

class MainActivity : ComponentActivity() {
  private val viewModel: CoachingViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainCoachingApp(viewModel = viewModel)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCoachingApp(viewModel: CoachingViewModel) {
  var currentTab by remember { mutableStateOf(AppTab.TIMER) }
  val stats by viewModel.statistics.collectAsStateWithLifecycle()
  val timerState by viewModel.timerState.collectAsStateWithLifecycle()

  // Dialog State
  var showEditDialog by remember { mutableStateOf(false) }
  var sessionToEdit by remember { mutableStateOf<CoachingSession?>(null) }
  var timerPrefillData by remember { mutableStateOf<PrefillTimerData?>(null) }

  // Handle Android system back button when on sub-screens
  if (currentTab != AppTab.TIMER) {
    BackHandler {
      currentTab = AppTab.TIMER
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(end = 12.dp)
          ) {
            Column {
              Text(
                text = "KAC 코칭로그",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "한국코치협회 50시간 실습 기록",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // Quick Milestone Indicator badge
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (stats.isCompleted) BadgePaid.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.clickable { currentTab = AppTab.DASHBOARD }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (stats.isCompleted) BadgePaid else MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                  text = "${String.format(Locale.US, "%.1f", stats.totalHours)} / 50.0h",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (stats.isCompleted) BadgePaid else MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      Column {
        // Floating Mini Timer Banner if running and not on TIMER tab
        val isTimerActive = timerState.isRunning || (timerState.isPaused && timerState.elapsedSeconds > 0)
        AnimatedVisibility(visible = isTimerActive && currentTab != AppTab.TIMER) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { currentTab = AppTab.TIMER }
              .testTag("floating_timer_banner")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (timerState.isRunning) BadgePaid else MaterialTheme.colorScheme.tertiary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (timerState.clientName.isNotBlank()) "코칭 중: ${timerState.clientName}" else "코칭 타임로그 진행 중",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              }

              val mins = timerState.elapsedSeconds / 60
              val secs = timerState.elapsedSeconds % 60
              val tenths = (timerState.elapsedMillis % 1000) / 100
              Text(
                text = String.format(Locale.US, "%02d:%02d.%d", mins, secs, tenths),
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        // Bottom Navigation Bar
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 6.dp,
          modifier = Modifier.testTag("bottom_nav_bar")
        ) {
          AppTab.entries.forEach { tab ->
            NavigationBarItem(
              selected = currentTab == tab,
              onClick = { currentTab = tab },
              icon = {
                Icon(
                  imageVector = tab.icon,
                  contentDescription = tab.label
                )
              },
              label = {
                Text(
                  text = tab.label,
                  fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.sp
                )
              },
              modifier = Modifier.testTag("nav_item_${tab.name}")
            )
          }
        }
      }
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        AppTab.TIMER -> TimerScreen(
          viewModel = viewModel,
          onOpenCompleteDialog = { prefill ->
            timerPrefillData = prefill
            sessionToEdit = null
            showEditDialog = true
          },
          onNavigateToStats = { currentTab = AppTab.DASHBOARD }
        )

        AppTab.LOGS -> LogListScreen(
          viewModel = viewModel,
          onAddNewSession = {
            sessionToEdit = null
            timerPrefillData = null
            showEditDialog = true
          },
          onEditSession = { session ->
            sessionToEdit = session
            timerPrefillData = null
            showEditDialog = true
          }
        )

        AppTab.DASHBOARD -> DashboardScreen(
          viewModel = viewModel
        )

        AppTab.EXPORT -> ExportReportScreen(
          viewModel = viewModel
        )
      }
    }
  }

  // Edit / Add Session Dialog
  if (showEditDialog) {
    EditSessionDialog(
      initialSession = sessionToEdit,
      prefillFromTimer = timerPrefillData,
      onDismiss = {
        showEditDialog = false
        sessionToEdit = null
        timerPrefillData = null
      },
      onSave = { savedSession ->
        viewModel.saveSession(savedSession)
        showEditDialog = false
        sessionToEdit = null
        timerPrefillData = null
        // If it was saved from timer, reset timer to clean state
        if (timerPrefillData != null) {
          viewModel.resetTimer()
        }
      }
    )
  }
}
