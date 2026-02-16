package com.fahim.chesstimer.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fahim.chesstimer.ChessTimerViewModel
import com.fahim.chesstimer.ui.theme.LocalChessTimerColors
import com.fahim.chesstimer.ui.theme.ThemeStyle

// ==========================================
// Time Preset Data
// ==========================================

private data class TimePreset(
    val label: String,
    val minutes: Int,
    val seconds: Int = 0,
    val increment: Int = 0
)

private data class TimeCategory(
    val name: String,
    val presets: List<TimePreset>
)

private val timeCategories = listOf(
    TimeCategory(
        "Bullet", listOf(
            TimePreset("1+0", 1),
            TimePreset("1+1", 1, increment = 1),
            TimePreset("2+1", 2, increment = 1)
        )
    ),
    TimeCategory(
        "Blitz", listOf(
            TimePreset("3+0", 3),
            TimePreset("3+2", 3, increment = 2),
            TimePreset("5+0", 5),
            TimePreset("5+3", 5, increment = 3)
        )
    ),
    TimeCategory(
        "Rapid", listOf(
            TimePreset("10+0", 10),
            TimePreset("10+5", 10, increment = 5),
            TimePreset("15+10", 15, increment = 10),
            TimePreset("30+0", 30)
        )
    ),
    TimeCategory(
        "Classical", listOf(
            TimePreset("60+0", 60),
            TimePreset("90+30", 90, increment = 30)
        )
    )
)

// ==========================================
// Settings Screen
// ==========================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: ChessTimerViewModel,
    currentTheme: ThemeStyle,
    onThemeChanged: (ThemeStyle) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current

    // Form state
    var whiteMinutes by remember { mutableStateOf("5") }
    var whiteSeconds by remember { mutableStateOf("0") }
    var whiteIncrement by remember { mutableStateOf("0") }
    var blackMinutes by remember { mutableStateOf("5") }
    var blackSeconds by remember { mutableStateOf("0") }
    var blackIncrement by remember { mutableStateOf("0") }
    var sameTimeForBoth by remember { mutableStateOf(true) }
    var selectedPresetLabel by remember { mutableStateOf<String?>("5+0") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Time Control") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // ---- Theme Selector ----
            Text(
                text = "THEME",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = currentTheme == ThemeStyle.GRANDMASTER,
                    onClick = { onThemeChanged(ThemeStyle.GRANDMASTER) },
                    label = { Text("Grandmaster") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = currentTheme == ThemeStyle.BLITZ,
                    onClick = { onThemeChanged(ThemeStyle.BLITZ) },
                    label = { Text("Blitz Arena") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                FilterChip(
                    selected = currentTheme == ThemeStyle.FOREST_CLASSICAL,
                    onClick = { onThemeChanged(ThemeStyle.FOREST_CLASSICAL) },
                    label = { Text("Forest") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ---- Time Presets by Category ----
            timeCategories.forEach { category ->
                Text(
                    text = category.name.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    category.presets.forEach { preset ->
                        FilterChip(
                            selected = selectedPresetLabel == preset.label,
                            onClick = {
                                selectedPresetLabel = preset.label
                                whiteMinutes = preset.minutes.toString()
                                whiteSeconds = preset.seconds.toString()
                                whiteIncrement = preset.increment.toString()
                                if (sameTimeForBoth) {
                                    blackMinutes = preset.minutes.toString()
                                    blackSeconds = preset.seconds.toString()
                                    blackIncrement = preset.increment.toString()
                                }
                            },
                            label = { Text(preset.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // ---- Custom Time Configuration ----
            Text(
                text = "CUSTOM",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Same time checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Checkbox(
                    checked = sameTimeForBoth,
                    onCheckedChange = { checked ->
                        sameTimeForBoth = checked
                        if (checked) {
                            blackMinutes = whiteMinutes
                            blackSeconds = whiteSeconds
                            blackIncrement = whiteIncrement
                        }
                    }
                )
                Text(
                    text = "Same time for both players",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // White player card
            PlayerTimeCard(
                playerLabel = "White",
                isWhitePlayer = true,
                minutes = whiteMinutes,
                seconds = whiteSeconds,
                increment = whiteIncrement,
                onMinutesChange = { value ->
                    whiteMinutes = value
                    selectedPresetLabel = null
                    if (sameTimeForBoth) blackMinutes = value
                },
                onSecondsChange = { value ->
                    whiteSeconds = value
                    selectedPresetLabel = null
                    if (sameTimeForBoth) blackSeconds = value
                },
                onIncrementChange = { value ->
                    whiteIncrement = value
                    selectedPresetLabel = null
                    if (sameTimeForBoth) blackIncrement = value
                }
            )

            // Black player card (hidden when same time)
            if (!sameTimeForBoth) {
                Spacer(modifier = Modifier.height(12.dp))

                PlayerTimeCard(
                    playerLabel = "Black",
                    isWhitePlayer = false,
                    minutes = blackMinutes,
                    seconds = blackSeconds,
                    increment = blackIncrement,
                    onMinutesChange = { blackMinutes = it; selectedPresetLabel = null },
                    onSecondsChange = { blackSeconds = it; selectedPresetLabel = null },
                    onIncrementChange = { blackIncrement = it; selectedPresetLabel = null }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Apply button
            Button(
                onClick = {
                    val wMin = whiteMinutes.toIntOrNull() ?: 0
                    val wSec = whiteSeconds.toIntOrNull() ?: 0
                    val wInc = whiteIncrement.toIntOrNull() ?: 0
                    val bMin = if (sameTimeForBoth) wMin else (blackMinutes.toIntOrNull() ?: 0)
                    val bSec = if (sameTimeForBoth) wSec else (blackSeconds.toIntOrNull() ?: 0)

                    if (wMin == 0 && wSec == 0) {
                        Toast.makeText(context, "White time cannot be zero", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (bMin == 0 && bSec == 0) {
                        Toast.makeText(context, "Black time cannot be zero", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.applySettings(
                        p1Time = (wMin * 60L + wSec) * 1000L,
                        p2Time = (bMin * 60L + bSec) * 1000L,
                        increment = wInc * 1000L
                    )
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Apply Settings",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PlayerTimeCard(
    playerLabel: String,
    isWhitePlayer: Boolean,
    minutes: String,
    seconds: String,
    increment: String,
    onMinutesChange: (String) -> Unit,
    onSecondsChange: (String) -> Unit,
    onIncrementChange: (String) -> Unit
) {
    val extendedColors = LocalChessTimerColors.current

    val cardBackground = if (isWhitePlayer) {
        extendedColors.whitePlayerBackground
    } else {
        extendedColors.blackPlayerBackground
    }
    val cardContent = if (isWhitePlayer) {
        extendedColors.whitePlayerText
    } else {
        extendedColors.blackPlayerText
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = playerLabel,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = cardContent,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TimeInputField(
                    label = "Min",
                    value = minutes,
                    onValueChange = onMinutesChange,
                    textColor = cardContent,
                    modifier = Modifier.weight(1f)
                )
                TimeInputField(
                    label = "Sec",
                    value = seconds,
                    onValueChange = onSecondsChange,
                    textColor = cardContent,
                    modifier = Modifier.weight(1f)
                )
                TimeInputField(
                    label = "+Sec",
                    value = increment,
                    onValueChange = onIncrementChange,
                    textColor = cardContent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TimeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Only accept numeric input, max 3 digits
                if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                    onValueChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = textColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
