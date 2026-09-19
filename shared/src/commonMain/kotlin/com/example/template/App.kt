package com.example.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * The whole app: a top bar, the menu that slides in from the left, and the page on screen.
 *
 * Everything platform-specific comes in from the caller: [back] takes the system's back gesture while
 * enabled, [onExit] closes the app from the menu, [libraries] reads the list of libraries it shows.
 * [preferences] are the settings, [onPreferences] takes a change, including the page left for [Start.Last].
 */
@Composable
fun App(
    back: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {},
    libraries: suspend () -> String = { "{}" },
    preferences: Preferences = Preferences(),
    onPreferences: (Preferences) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    // Saveable, so the page survives the activity being recreated, as on rotation or a theme change.
    var page by rememberSaveable {
        mutableStateOf(if (preferences.start == Start.Last) preferences.lastPage else Page.Home)
    }
    val drawer = rememberDrawerState(DrawerValue.Closed)

    back(page != Page.Home) { page = page.parent }
    val latest by rememberUpdatedState(preferences)
    val keep by rememberUpdatedState(onPreferences)
    LaunchedEffect(page) {
        if (page != Page.Settings && page != latest.lastPage) keep(latest.copy(lastPage = page))
    }
    val scroll = rememberPageScroll(page, preferences, onPreferences)

    MaterialTheme(colorScheme = colorSchemeOf(preferences.theme)) {
        ModalNavigationDrawer(
            drawerState = drawer,
            modifier = modifier,
            // Opened by the button only: a swipe from the left edge is Android's back gesture.
            gesturesEnabled = drawer.isOpen,
            drawerContent = {
                Menu(
                    drawer,
                    page,
                    preferences.folded,
                    onFold = { keep(latest.copy(folded = it)) },
                    onClose = { scope.launch { drawer.close() } },
                    onPage = {
                        page = it
                        scope.launch { drawer.close() }
                    },
                    onExit = onExit,
                )
            },
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    TopBar(
                        stringResource(page.title),
                        onMenu = { scope.launch { drawer.open() } },
                        onSettings = { page = Page.Settings },
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    ) {
                        PageContent(
                            page,
                            libraries,
                            preferences,
                            onPreferences,
                            onPage = { page = it },
                            scroll = scroll,
                        )
                    }
                }
            }
        }
    }
}
