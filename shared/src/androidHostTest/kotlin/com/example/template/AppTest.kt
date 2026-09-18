package com.example.template

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppTest {
    @Test
    fun opensAPageFromItsCardAndGoesBack() = runComposeUiTest {
        var goBack: (() -> Unit)? = null
        setContent { App(back = { enabled, onBack -> if (enabled) goBack = onBack }) }
        // The card's line, since the closed menu holds the title "Example" too.
        onNodeWithText("A counter and a line", substring = true).performClick()
        onNodeWithText("Tapped 0 times").assertExists()
        runOnIdle { goBack!!() }
        onNodeWithText("Tapped 0 times").assertDoesNotExist()
    }

    @Test
    fun remembersThePageLeft() = runComposeUiTest {
        val kept = mutableListOf<Preferences>()
        setContent { App(back = { _, _ -> }, onPreferences = { kept += it }) }
        onNodeWithContentDescription("Open menu").performClick()
        onNodeWithText("About").performClick()
        waitForIdle()
        assertEquals(Page.About, kept.last().lastPage)
    }

    @Test
    fun rendersAMarkdownPage() = runComposeUiTest {
        setContent { App(back = { _, _ -> }) }
        onNodeWithContentDescription("Open menu").performClick()
        onNodeWithText("About").performClick()
        onNodeWithText("How to use the app, page by page.").performClick()
        // The renderer parses off the main thread, so the text comes a moment after the page.
        waitUntilAtLeastOneExists(hasText("This page is example text", substring = true), timeoutMillis = 5_000)
    }
}
