package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A luxury photorealistic mechanical stopwatch featuring:
 * - Mechanical crown and pushers (tactile top start/pause button)
 * - Heavy multi-layer brushed stainless steel bezel with chamfered rim
 * - Concentric sunburst dial in deep obsidian coaching navy
 * - 60-second calibrated track with 1/5th second micro-ticks
 * - 30-minute chronograph sub-dial
 * - Inset recessed digital OLED display
 * - Precision racing red sweep needle with realistic drop shadow
 * - Convex domed sapphire crystal glass with spherical specular reflections
 */
@Composable
fun PhotorealisticStopwatch(
  elapsedMillis: Long,
  isRunning: Boolean,
  targetPresetMinutes: Int = 50,
  onTopPusherClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val textMeasurer = rememberTextMeasurer()

  // Calculate sweep angles
  val secondsFraction = (elapsedMillis % 60000L) / 60000f
  val secondAngle = secondsFraction * 360f

  val minutesFraction = (elapsedMillis / 60000f) % 30f / 30f
  val minuteSubdialAngle = minutesFraction * 360f

  // Subtle breathing animation on reflection when running
  val reflectionSheen = remember { Animatable(0f) }
  LaunchedEffect(isRunning) {
    if (isRunning) {
      reflectionSheen.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = tween(2500, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse
        )
      )
    } else {
      reflectionSheen.snapTo(0.2f)
    }
  }

  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(0.92f) // slightly taller for top crown/pusher
      .testTag("photorealistic_stopwatch_box")
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onTopPusherClick
        )
        .testTag("photorealistic_stopwatch_canvas")
    ) {
      val canvasWidth = size.width
      val canvasHeight = size.height

      // Center the main circular watch body in the lower portion
      val center = Offset(canvasWidth / 2f, canvasHeight * 0.55f)
      val watchRadius = min(canvasWidth, canvasHeight * 0.88f) * 0.44f

      // 1. Draw Mechanical Crown & Pushers (Top Assembly)
      drawMechanicalPushers(center, watchRadius, isRunning)

      // 2. Outer Ambient Case Shadow (Physical Elevation)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0x55000000),
            Color(0x30000000),
            Color.Transparent
          ),
          center = center.copy(y = center.y + watchRadius * 0.04f),
          radius = watchRadius * 1.12f
        ),
        center = center.copy(y = center.y + watchRadius * 0.04f),
        radius = watchRadius * 1.12f
      )

      // 3. Multi-layer Brushed Stainless Steel Casing (Bezel)
      drawBrushedSteelBezel(center, watchRadius)

      // 4. Dial Face (Obsidian Coaching Navy with concentric grain)
      drawStopwatchDial(
        center = center,
        radius = watchRadius * 0.85f,
        elapsedMillis = elapsedMillis,
        targetPresetMinutes = targetPresetMinutes,
        textMeasurer = textMeasurer,
        isRunning = isRunning
      )

      // 5. 30-Minute Sub-dial
      drawMinuteSubdial(
        center = Offset(center.x, center.y - watchRadius * 0.42f),
        radius = watchRadius * 0.24f,
        angle = minuteSubdialAngle,
        textMeasurer = textMeasurer
      )

      // 6. Chronograph Second Hand (Racing Red) with Needle Drop Shadow
      drawChronographNeedle(
        center = center,
        radius = watchRadius * 0.82f,
        angle = secondAngle
      )

      // 7. Center Hub / Pivot Cap
      drawCenterPivotCap(center, watchRadius * 0.07f)

      // 8. Convex Domed Sapphire Crystal Glass Cover (볼록한 글래스 덮개)
      drawConvexGlassEffect(
        center = center,
        radius = watchRadius * 0.85f,
        sheenOffset = reflectionSheen.value
      )
    }
  }
}

/**
 * 1. Mechanical Crown and Pusher Assembly at the top of the chronometer.
 */
private fun DrawScope.drawMechanicalPushers(
  center: Offset,
  radius: Float,
  isRunning: Boolean
) {
  val topCrownCenterY = center.y - radius * 1.05f
  val crownWidth = radius * 0.28f
  val crownHeight = radius * 0.16f

  // Steel Carabiner / Winding Loop above crown
  val ringRadius = radius * 0.20f
  val ringCenter = Offset(center.x, topCrownCenterY - crownHeight * 0.7f)
  drawCircle(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFFCBD5E1), Color(0xFF334155)),
      start = Offset(ringCenter.x - ringRadius, ringCenter.y),
      end = Offset(ringCenter.x + ringRadius, ringCenter.y)
    ),
    center = ringCenter,
    radius = ringRadius,
    style = Stroke(width = radius * 0.045f)
  )

  // Top Center Knurled Pusher Button
  val pusherDepressOffset = if (isRunning) radius * 0.015f else 0f
  val pusherRect = Path().apply {
    moveTo(center.x - crownWidth / 2f, topCrownCenterY + pusherDepressOffset)
    lineTo(center.x + crownWidth / 2f, topCrownCenterY + pusherDepressOffset)
    lineTo(center.x + crownWidth * 0.42f, topCrownCenterY - crownHeight + pusherDepressOffset)
    lineTo(center.x - crownWidth * 0.42f, topCrownCenterY - crownHeight + pusherDepressOffset)
    close()
  }

  // Pusher Shadow
  drawPath(
    path = pusherRect,
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFF1F5F9), Color(0xFF94A3B8), Color(0xFF475569)),
      startY = topCrownCenterY - crownHeight,
      endY = topCrownCenterY
    )
  )

  // Horizontal knurling lines on top pusher
  for (i in 1..4) {
    val lineY = topCrownCenterY - crownHeight + (crownHeight * (i / 5f)) + pusherDepressOffset
    drawLine(
      color = Color(0xFF1E293B),
      start = Offset(center.x - crownWidth * 0.38f, lineY),
      end = Offset(center.x + crownWidth * 0.38f, lineY),
      strokeWidth = 1.8f
    )
  }

  // Left Pusher (Lap / Reset at ~10 o'clock: -40 degrees)
  drawAngledPusher(center, radius, angleDeg = -40f, labelColor = Color(0xFF38BDF8))

  // Right Pusher (Start / Pause at ~2 o'clock: +40 degrees)
  drawAngledPusher(center, radius, angleDeg = 40f, labelColor = if (isRunning) Color(0xFFF59E0B) else Color(0xFF10B981))
}

private fun DrawScope.drawAngledPusher(
  center: Offset,
  radius: Float,
  angleDeg: Float,
  labelColor: Color
) {
  val rad = Math.toRadians(angleDeg.toDouble() - 90.0)
  val pusherDistance = radius * 1.02f
  val pusherPos = Offset(
    (center.x + cos(rad) * pusherDistance).toFloat(),
    (center.y + sin(rad) * pusherDistance).toFloat()
  )

  rotate(angleDeg, pivot = pusherPos) {
    drawRoundRect(
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFFE2E8F0), Color(0xFF64748B), Color(0xFF334155))
      ),
      topLeft = Offset(pusherPos.x - radius * 0.09f, pusherPos.y - radius * 0.08f),
      size = Size(radius * 0.18f, radius * 0.09f),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
    )
    // Colored top rim ring
    drawCircle(
      color = labelColor,
      radius = radius * 0.035f,
      center = Offset(pusherPos.x, pusherPos.y - radius * 0.04f)
    )
  }
}

/**
 * 2. Multi-layer Brushed Stainless Steel Casing (Bezel).
 */
private fun DrawScope.drawBrushedSteelBezel(center: Offset, radius: Float) {
  // Layer A: Outer Steel Case Rim
  drawCircle(
    brush = Brush.sweepGradient(
      colors = listOf(
        Color(0xFF94A3B8),
        Color(0xFFF1F5F9),
        Color(0xFF64748B),
        Color(0xFFCBD5E1),
        Color(0xFF334155),
        Color(0xFFE2E8F0),
        Color(0xFF94A3B8)
      ),
      center = center
    ),
    center = center,
    radius = radius
  )

  // Layer B: Chamfered Dark Titanium Step
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155)),
      center = center,
      radius = radius * 0.94f
    ),
    center = center,
    radius = radius * 0.94f,
    style = Stroke(width = radius * 0.04f)
  )

  // Layer C: Bright Inner Chrome Lip where glass seats
  drawCircle(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFFFFFFFF), Color(0xFF94A3B8), Color(0xFFE2E8F0)),
      start = Offset(center.x - radius * 0.85f, center.y - radius * 0.85f),
      end = Offset(center.x + radius * 0.85f, center.y + radius * 0.85f)
    ),
    center = center,
    radius = radius * 0.86f,
    style = Stroke(width = radius * 0.018f)
  )
}

/**
 * 3. The Stopwatch Dial Face with Tachymeter 60s markings and Digital Display.
 */
private fun DrawScope.drawStopwatchDial(
  center: Offset,
  radius: Float,
  elapsedMillis: Long,
  targetPresetMinutes: Int,
  textMeasurer: androidx.compose.ui.text.TextMeasurer,
  isRunning: Boolean
) {
  // Base Dial: Deep Obsidian Navy with radial falloff
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFF111C36), // center deep navy
        Color(0xFF0B1124),
        Color(0xFF030712)  // outer dark rim
      ),
      center = center,
      radius = radius
    ),
    center = center,
    radius = radius
  )

  // Subtle concentric micro-grooves (azurage finish)
  for (i in 1..4) {
    drawCircle(
      color = Color(0x12FFFFFF),
      center = center,
      radius = radius * (0.25f + i * 0.15f),
      style = Stroke(width = 0.8f)
    )
  }

  // 60-Second Outer Track Ticks and Numbers
  val totalTicks = 60 * 5 // 300 ticks (1/5th second micro-ticks)
  for (i in 0 until 300) {
    val angle = (i * 360f / 300f) - 90f
    val rad = Math.toRadians(angle.toDouble())
    val isMajorSecond = (i % 5 == 0)
    val isFiveSecondBlock = (i % 25 == 0)

    val innerR = when {
      isFiveSecondBlock -> radius * 0.84f
      isMajorSecond -> radius * 0.87f
      else -> radius * 0.91f
    }
    val outerR = radius * 0.96f

    val strokeW = when {
      isFiveSecondBlock -> 2.6f
      isMajorSecond -> 1.6f
      else -> 0.7f
    }

    val tickColor = when {
      isFiveSecondBlock -> Color(0xFFF8FAFC)
      isMajorSecond -> Color(0xFFCBD5E1)
      else -> Color(0x6694A3B8)
    }

    val startX = (center.x + cos(rad) * innerR).toFloat()
    val startY = (center.y + sin(rad) * innerR).toFloat()
    val endX = (center.x + cos(rad) * outerR).toFloat()
    val endY = (center.y + sin(rad) * outerR).toFloat()

    drawLine(
      color = tickColor,
      start = Offset(startX, startY),
      end = Offset(endX, endY),
      strokeWidth = strokeW,
      cap = StrokeCap.Round
    )

    // Numbers: 60, 05, 10, ..., 55
    if (isFiveSecondBlock) {
      val secondNum = (i / 5)
      val numText = if (secondNum == 0) "60" else String.format(Locale.US, "%02d", secondNum)
      val textR = radius * 0.74f
      val textX = (center.x + cos(rad) * textR).toFloat()
      val textY = (center.y + sin(rad) * textR).toFloat()

      val measuredText = textMeasurer.measure(
        text = numText,
        style = TextStyle(
          color = if (secondNum == 0) Color(0xFFEF4444) else Color(0xFFE2E8F0),
          fontSize = (radius * 0.075f).sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      )
      drawText(
        textLayoutResult = measuredText,
        topLeft = Offset(
          textX - measuredText.size.width / 2f,
          textY - measuredText.size.height / 2f
        )
      )
    }
  }

  // Dial Branding
  val brandText = textMeasurer.measure(
    text = "KAC CHRONO",
    style = TextStyle(
      color = Color(0xFF38BDF8),
      fontSize = (radius * 0.065f).sp,
      fontWeight = FontWeight.ExtraBold,
      letterSpacing = 1.2.sp
    )
  )
  drawText(
    textLayoutResult = brandText,
    topLeft = Offset(center.x - brandText.size.width / 2f, center.y - radius * 0.16f)
  )

  val certText = textMeasurer.measure(
    text = "50H CERTIFIED",
    style = TextStyle(
      color = Color(0xFF94A3B8),
      fontSize = (radius * 0.045f).sp,
      fontWeight = FontWeight.SemiBold,
      letterSpacing = 0.8.sp
    )
  )
  drawText(
    textLayoutResult = certText,
    topLeft = Offset(center.x - certText.size.width / 2f, center.y - radius * 0.09f)
  )

  // Inset OLED / LCD Digital Display Window (Lower Half)
  drawDigitalDisplayWindow(center, radius, elapsedMillis, textMeasurer, isRunning)
}

/**
 * Recessed Digital Inset Window on the dial showing HH:MM:SS.ss
 */
private fun DrawScope.drawDigitalDisplayWindow(
  center: Offset,
  radius: Float,
  elapsedMillis: Long,
  textMeasurer: androidx.compose.ui.text.TextMeasurer,
  isRunning: Boolean
) {
  val winWidth = radius * 0.88f
  val winHeight = radius * 0.28f
  val winTop = center.y + radius * 0.32f
  val winLeft = center.x - winWidth / 2f

  // Recessed frame border with 3D bevel
  drawRoundRect(
    color = Color(0xFF1E293B),
    topLeft = Offset(winLeft - 2f, winTop - 2f),
    size = Size(winWidth + 4f, winHeight + 4f),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
    style = Stroke(width = 2.5f)
  )

  // Deep recessed screen background
  drawRoundRect(
    color = Color(0xFF030712),
    topLeft = Offset(winLeft, winTop),
    size = Size(winWidth, winHeight),
    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
  )

  // Time format
  val totalSec = elapsedMillis / 1000
  val hours = totalSec / 3600
  val minutes = (totalSec % 3600) / 60
  val seconds = totalSec % 60
  val hundredths = (elapsedMillis % 1000) / 10

  val formattedTime = String.format(Locale.US, "%02d:%02d:%02d.%02d", hours, minutes, seconds, hundredths)

  val digitsText = textMeasurer.measure(
    text = formattedTime,
    style = TextStyle(
      color = if (isRunning) Color(0xFF38BDF8) else Color(0xFFF8FAFC),
      fontSize = (radius * 0.092f).sp,
      fontWeight = FontWeight.ExtraBold,
      fontFamily = FontFamily.Monospace,
      letterSpacing = 0.5.sp
    )
  )

  drawText(
    textLayoutResult = digitsText,
    topLeft = Offset(
      center.x - digitsText.size.width / 2f,
      winTop + (winHeight - digitsText.size.height) / 2f
    )
  )
}

/**
 * 4. 30-Minute Chronograph Sub-dial (Upper center).
 */
private fun DrawScope.drawMinuteSubdial(
  center: Offset,
  radius: Float,
  angle: Float,
  textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
  // Subdial recessed base
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFF0F172A), Color(0xFF020617)),
      center = center,
      radius = radius
    ),
    center = center,
    radius = radius
  )
  drawCircle(
    color = Color(0x33FFFFFF),
    center = center,
    radius = radius,
    style = Stroke(width = 1.2f)
  )

  // Concentric grooving inside subdial
  drawCircle(
    color = Color(0x18FFFFFF),
    center = center,
    radius = radius * 0.5f,
    style = Stroke(width = 0.8f)
  )

  // Subdial marks: 30 minutes (0, 10, 20, 30)
  for (m in 0 until 30) {
    val a = (m * 360f / 30f) - 90f
    val rad = Math.toRadians(a.toDouble())
    val isMajor = (m % 5 == 0)
    val innerR = if (isMajor) radius * 0.72f else radius * 0.82f
    val outerR = radius * 0.92f

    drawLine(
      color = if (isMajor) Color(0xFFF1F5F9) else Color(0x6694A3B8),
      start = Offset((center.x + cos(rad) * innerR).toFloat(), (center.y + sin(rad) * innerR).toFloat()),
      end = Offset((center.x + cos(rad) * outerR).toFloat(), (center.y + sin(rad) * outerR).toFloat()),
      strokeWidth = if (isMajor) 1.5f else 0.8f
    )

    if (m % 10 == 0) {
      val textR = radius * 0.52f
      val tx = (center.x + cos(rad) * textR).toFloat()
      val ty = (center.y + sin(rad) * textR).toFloat()
      val label = if (m == 0) "30" else "$m"
      val t = textMeasurer.measure(
        text = label,
        style = TextStyle(
          color = Color(0xFF94A3B8),
          fontSize = (radius * 0.28f).sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      )
      drawText(t, topLeft = Offset(tx - t.size.width / 2f, ty - t.size.height / 2f))
    }
  }

  // Subdial Needle (Silver with Drop Shadow)
  rotate(angle, pivot = center) {
    // Drop shadow
    drawLine(
      color = Color(0x66000000),
      start = Offset(center.x + 1.5f, center.y + 1.5f),
      end = Offset(center.x + 1.5f, center.y - radius * 0.78f + 1.5f),
      strokeWidth = 2.4f,
      cap = StrokeCap.Round
    )
    // Needle
    drawLine(
      color = Color(0xFFF8FAFC),
      start = center,
      end = Offset(center.x, center.y - radius * 0.78f),
      strokeWidth = 2.0f,
      cap = StrokeCap.Round
    )
    // Center cap
    drawCircle(
      color = Color(0xFF38BDF8),
      radius = radius * 0.14f,
      center = center
    )
  }
}

/**
 * 5. Racing Red Chronograph Sweep Needle with realistic 3D Drop Shadow.
 */
private fun DrawScope.drawChronographNeedle(
  center: Offset,
  radius: Float,
  angle: Float
) {
  val needleLength = radius * 0.98f
  val tailLength = radius * 0.22f

  rotate(angle, pivot = center) {
    // --- Realistic Needle Drop Shadow (offset +3px, +3px) ---
    val shadowOffset = Offset(3f, 3f)
    val shadowPath = Path().apply {
      moveTo(center.x + shadowOffset.x, center.y - needleLength + shadowOffset.y)
      lineTo(center.x + radius * 0.024f + shadowOffset.x, center.y + shadowOffset.y)
      lineTo(center.x + shadowOffset.x, center.y + tailLength + shadowOffset.y)
      lineTo(center.x - radius * 0.024f + shadowOffset.x, center.y + shadowOffset.y)
      close()
    }
    drawPath(path = shadowPath, color = Color(0x55000000))

    // --- Main Racing Red Needle ---
    val needlePath = Path().apply {
      moveTo(center.x, center.y - needleLength)
      lineTo(center.x + radius * 0.024f, center.y)
      lineTo(center.x, center.y + tailLength)
      lineTo(center.x - radius * 0.024f, center.y)
      close()
    }

    drawPath(
      path = needlePath,
      brush = Brush.linearGradient(
        colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626), Color(0xFFB91C1C)),
        start = Offset(center.x - radius * 0.024f, center.y),
        end = Offset(center.x + radius * 0.024f, center.y)
      )
    )

    // Fine needle tip accent (white luminous tip)
    drawLine(
      color = Color(0xFFFFFFFF),
      start = Offset(center.x, center.y - needleLength * 0.75f),
      end = Offset(center.x, center.y - needleLength),
      strokeWidth = 2.0f,
      cap = StrokeCap.Round
    )

    // Tail diamond / counterbalance ring
    drawCircle(
      color = Color(0xFFEF4444),
      radius = radius * 0.045f,
      center = Offset(center.x, center.y + tailLength * 0.65f)
    )
    drawCircle(
      color = Color(0xFF030712),
      radius = radius * 0.022f,
      center = Offset(center.x, center.y + tailLength * 0.65f)
    )
  }
}

/**
 * Center Pivot Cap with polished chrome hub and steel screw.
 */
private fun DrawScope.drawCenterPivotCap(center: Offset, radius: Float) {
  // Polished chrome hub
  drawCircle(
    brush = Brush.linearGradient(
      colors = listOf(Color(0xFFF8FAFC), Color(0xFF94A3B8), Color(0xFF334155)),
      start = Offset(center.x - radius, center.y - radius),
      end = Offset(center.x + radius, center.y + radius)
    ),
    center = center,
    radius = radius
  )

  // Inner dark groove
  drawCircle(
    color = Color(0xFF0F172A),
    center = center,
    radius = radius * 0.6f
  )

  // Center steel screw dot
  drawCircle(
    color = Color(0xFFE2E8F0),
    center = center,
    radius = radius * 0.35f
  )
}

/**
 * 6. Convex Domed Sapphire Crystal Glass Cover (볼록한 글래스 덮개 효과)
 *
 * Simulates a thick, highly-polished curved/domed mineral watch glass with:
 * - Spherical optical edge refraction (dark/cyan edge vignette)
 * - Upper curved crescent specular reflection (ambient overhead light curvature)
 * - Lower perimeter reflection bounce
 * - Fine diagonal crystal glare
 * - Outer edge refraction bevel ring
 */
private fun DrawScope.drawConvexGlassEffect(
  center: Offset,
  radius: Float,
  sheenOffset: Float
) {
  // 1. Spherical Lens Vignette (Edge curvature & glass refraction thickness)
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x00000000), // crystal clear in center
        Color(0x00000000),
        Color(0x0838BDF8), // subtle blue-tint sapphire coating
        Color(0x180284C7), // refraction flare
        Color(0x38000000)  // curved edge shadow
      ),
      center = center,
      radius = radius
    ),
    center = center,
    radius = radius
  )

  // 2. Primary Upper Convex Specular Curved Highlight (상단 볼록 유리 반사광)
  // Drawn as a curved elliptical arc across the top hemisphere
  val highlightCenter = Offset(center.x, center.y - radius * 0.40f)
  val highlightRadiusX = radius * 0.82f
  val highlightRadiusY = radius * 0.50f

  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x75FFFFFF), // crisp white specular highlight
        Color(0x30FFFFFF),
        Color(0x08FFFFFF),
        Color.Transparent
      ),
      center = highlightCenter,
      radius = highlightRadiusX
    ),
    topLeft = Offset(center.x - highlightRadiusX, center.y - radius * 0.88f),
    size = Size(highlightRadiusX * 2f, highlightRadiusY * 2f)
  )

  // 3. Lower Rim Bounce Reflection (하단 곡면 반사광)
  drawArc(
    brush = Brush.linearGradient(
      colors = listOf(
        Color.Transparent,
        Color(0x30FFFFFF),
        Color(0x55FFFFFF),
        Color(0x20FFFFFF),
        Color.Transparent
      ),
      start = Offset(center.x - radius * 0.6f, center.y + radius * 0.7f),
      end = Offset(center.x + radius * 0.6f, center.y + radius * 0.95f)
    ),
    startAngle = 40f,
    sweepAngle = 100f,
    useCenter = false,
    topLeft = Offset(center.x - radius * 0.82f, center.y - radius * 0.82f),
    size = Size(radius * 1.64f, radius * 1.64f),
    style = Stroke(width = radius * 0.035f, cap = StrokeCap.Round)
  )

  // 4. Diagonal Crystal Glass Glare Streak (대각선 유리 광택 빗살)
  val glareSheenAlpha = (0.08f + sheenOffset * 0.06f)
  drawArc(
    brush = Brush.linearGradient(
      colors = listOf(
        Color.Transparent,
        Color.White.copy(alpha = glareSheenAlpha),
        Color.White.copy(alpha = glareSheenAlpha * 1.8f),
        Color.Transparent
      ),
      start = Offset(center.x - radius * 0.9f, center.y - radius * 0.9f),
      end = Offset(center.x + radius * 0.3f, center.y + radius * 0.3f)
    ),
    startAngle = 160f,
    sweepAngle = 120f,
    useCenter = true,
    topLeft = Offset(center.x - radius * 0.84f, center.y - radius * 0.84f),
    size = Size(radius * 1.68f, radius * 1.68f)
  )

  // 5. Perimeter Crystal Refraction Ring (유리와 메탈이 만나는 림의 투명 굴절 링)
  drawCircle(
    brush = Brush.sweepGradient(
      colors = listOf(
        Color(0x40FFFFFF),
        Color(0x10FFFFFF),
        Color(0x50FFFFFF),
        Color(0x15FFFFFF),
        Color(0x40FFFFFF)
      ),
      center = center
    ),
    center = center,
    radius = radius * 0.845f,
    style = Stroke(width = 1.6f)
  )
}
