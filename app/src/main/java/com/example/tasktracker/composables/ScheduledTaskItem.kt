package com.example.tasktracker.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime

@Composable
fun ScheduledTaskItem(
    title: String,
    description: String,
    time: LocalDateTime
) {
    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        Column (
            modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            Text(
                text = title,
                fontSize = 40.sp,
                maxLines = 1
            )
            Text(
                modifier = Modifier.alpha(0.7f),
                text = description,
                fontSize = 30.sp,
                maxLines = 1
            )
        }
    }
}

@Preview(
    widthDp = 412,
    heightDp = 100,
    showBackground = true
)
@Composable
fun ScheduledTaskItemPreview() {
    ScheduledTaskItem(
        title = "Yo",
        description = "Description",
        time = LocalDateTime.now()
    )
}