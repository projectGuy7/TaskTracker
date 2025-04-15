package com.example.tasktracker.composables.mainScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.time.LocalTime

@Composable
fun ScheduledTaskItem(
    modifier: Modifier = Modifier,
    title: String,
    titleFontSize: TextUnit,
    description: String,
    descriptionFontSize: TextUnit,
    time: LocalTime,
    timeFontSize: TextUnit
) {
    val hours: String
    val minutes: String
    if(time.hour < 10) {
        hours = "0" + time.hour
    } else {
        hours = "${time.hour}"
    }
    if(time.minute < 10) {
        minutes = "0" + time.minute
    } else {
        minutes = "${time.minute}"
    }
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column (
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Text(
                text = title,
                fontSize = titleFontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.alpha(0.7f),
                text = description,
                fontSize = descriptionFontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "$hours:$minutes",
            fontSize = timeFontSize,
        )
    }
}