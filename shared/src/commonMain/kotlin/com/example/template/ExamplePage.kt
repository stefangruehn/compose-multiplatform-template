package com.example.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/** A page to copy: a counter with its text from string resources, and a line from platform code. */
@Composable
fun ExamplePage() {
    var count by rememberSaveable { mutableIntStateOf(0) }
    CardColumn {
        PageCard(Page.Example.icon, stringResource(Res.string.example_counter)) {
            Text(
                pluralStringResource(Res.plurals.example_count, count, count),
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(stringResource(Res.string.example_tap), onClick = { count++ })
                ActionButton(
                    stringResource(Res.string.example_reset),
                    onClick = { count = 0 },
                    enabled = count > 0,
                    filled = false,
                )
            }
        }
        PageCard(Res.drawable.info, stringResource(Res.string.example_platform)) {
            Text(
                stringResource(Res.string.example_platform_detail, platformName()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
