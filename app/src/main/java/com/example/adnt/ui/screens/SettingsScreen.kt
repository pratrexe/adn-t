package com.example.adnt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.adnt.data.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    onAppExclusion: () -> Unit
) {
    var blockTrackers by remember { mutableStateOf(settingsManager.blockTrackers) }
    var startOnBoot by remember { mutableStateOf(settingsManager.startOnBoot) }
    var dnsProvider by remember { mutableStateOf(settingsManager.dnsProvider) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SettingsCategory("General")
            
            SettingsToggleItem(
                title = "Start on Boot",
                subtitle = "Automatically start VPN when device boots",
                checked = startOnBoot,
                onCheckedChange = { 
                    startOnBoot = it
                    settingsManager.startOnBoot = it
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsCategory("Ad-Blocking")

            SettingsToggleItem(
                title = "Block Trackers",
                subtitle = "Prevent websites from tracking your activity",
                checked = blockTrackers,
                onCheckedChange = { 
                    blockTrackers = it
                    settingsManager.blockTrackers = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAppExclusion,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Text("Manage Excluded Apps (Split Tunneling)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("DNS Provider", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            
            DNSOption(
                title = "Google DNS",
                value = SettingsManager.DNS_GOOGLE,
                selected = dnsProvider == SettingsManager.DNS_GOOGLE,
                onSelect = { 
                    dnsProvider = it
                    settingsManager.dnsProvider = it
                }
            )
            DNSOption(
                title = "Cloudflare DNS",
                value = SettingsManager.DNS_CLOUDFLARE,
                selected = dnsProvider == SettingsManager.DNS_CLOUDFLARE,
                onSelect = { 
                    dnsProvider = it
                    settingsManager.dnsProvider = it
                }
            )
            DNSOption(
                title = "AdGuard DNS",
                value = SettingsManager.DNS_ADGUARD,
                selected = dnsProvider == SettingsManager.DNS_ADGUARD,
                onSelect = { 
                    dnsProvider = it
                    settingsManager.dnsProvider = it
                }
            )
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun DNSOption(
    title: String,
    value: String,
    selected: Boolean,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = { onSelect(value) })
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
    }
}
