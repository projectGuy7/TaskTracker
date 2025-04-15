package com.example.tasktracker.composables.mainScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
   Pass curr hour in AM/PM type, not 20:00, but 8:00
 */

@Composable
fun CustomCircularProgressBarClock (
    currHour: Byte,
    currMin: Byte,
    fontSize: TextUnit = 24.sp,
    radius: Dp = 50.dp,
    color: Color = Color.Blue,
    strokeWidth: Dp = 8.dp,
    animDuration: Int = 1000
) {
    val currTime: Float = (currHour * 60 + currMin).toFloat() / 720
    val prevTime: Float = when(currMin + currHour) {
        0 -> 0.0F
        else -> ((currHour * 60 + currMin).toFloat() - 1) / 720
    }
    val currentPercentage = animateFloatAsState(
        targetValue = currTime,
        animationSpec = tween(durationMillis = animDuration)
    )

    Box (
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Canvas (modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * prevTime,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = 360f * prevTime,
                sweepAngle = currentPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$currHour:$currMin",
            fontSize = fontSize
        )
    }
}