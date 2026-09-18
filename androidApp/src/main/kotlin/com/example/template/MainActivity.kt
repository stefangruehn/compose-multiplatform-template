package com.example.template

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

/** The Android entry point: reads and keeps the settings, and hands the platform's pieces to [App]. */
class MainActivity : ComponentActivity() {
    private val settings by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    private var preferences by mutableStateOf(Preferences())

    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = Preferences(
            theme = Theme.entries.find { it.name == settings.getString(THEME, null) } ?: Theme.System,
            start = Start.entries.find { it.name == settings.getString(START, null) } ?: Start.Home,
            lastPage = Page.entries.find { it.name == settings.getString(LAST_PAGE, null) } ?: Page.Home,
        )
        edgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(
                back = { enabled, onBack -> BackHandler(enabled, onBack) },
                onExit = { finish() },
                libraries = { resources.openRawResource(R.raw.aboutlibraries).bufferedReader().use { it.readText() } },
                preferences = preferences,
                onPreferences = {
                    val restyle = it.theme != preferences.theme
                    preferences = it
                    settings.edit {
                        putString(THEME, it.theme.name)
                        putString(START, it.start.name)
                        putString(LAST_PAGE, it.lastPage.name)
                    }
                    if (restyle) edgeToEdge()
                },
            )
        }
    }

    /** Draws behind the system bars, with their icons light on a dark theme and dark on a light one. */
    private fun edgeToEdge() {
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val night = mode == Configuration.UI_MODE_NIGHT_YES
        val bars = if (preferences.theme.isDark(night)) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        }
        enableEdgeToEdge(bars, bars)
    }

    private companion object {
        const val THEME = "theme"
        const val START = "start"
        const val LAST_PAGE = "last_page"
    }
}
