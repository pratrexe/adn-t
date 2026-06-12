package com.example.adnt.ui.screens

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.adnt.data.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppExclusionScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val pm = context.packageManager
    var searchQuery by remember { mutableStateOf("") }
    var excludedApps by remember { mutableStateOf(settingsManager.excludedApps) }

    val allApps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .sortedBy { pm.getApplicationLabel(it).toString() }
    }

    val filteredApps = allApps.filter {
        pm.getApplicationLabel(it).toString().contains(searchQuery, ignoreCase = true) ||
                it.packageName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Split Tunneling") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search apps...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Excluded apps bypass the VPN entirely. This helps if an app is crashing or slow.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(filteredApps) { app ->
                AppExclusionItem(
                    app = app,
                    pm = pm,
                    isExcluded = excludedApps.contains(app.packageName),
                    onToggle = { isExcluded ->
                        if (isExcluded) {
                            settingsManager.addExcludedApp(app.packageName)
                        } else {
                            settingsManager.removeExcludedApp(app.packageName)
                        }
                        excludedApps = settingsManager.excludedApps
                    }
                )
            }
        }
    }
}

@Composable
fun AppExclusionItem(
    app: ApplicationInfo,
    pm: PackageManager,
    isExcluded: Boolean,
    onToggle: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(pm.getApplicationLabel(app).toString()) },
        supportingContent = { Text(app.packageName, style = MaterialTheme.typography.bodySmall) },
        leadingContent = {
            val icon = remember(app.packageName) {
                pm.getApplicationIcon(app).toBitmap().asImageBitmap()
            }
            Image(
                bitmap = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        },
        trailingContent = {
            Switch(
                checked = isExcluded,
                onCheckedChange = onToggle
            )
        }
    )
}
