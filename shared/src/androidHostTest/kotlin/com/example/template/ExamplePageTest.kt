package com.example.template

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
// Robolectric needs Java 21 from SDK 35 on; the build compiles for 17.
@Config(sdk = [34])
class ExamplePageTest {
    @Test
    fun countsTapsAndResets() = runComposeUiTest {
        setContent { MaterialTheme { ExamplePage() } }
        onNodeWithText("Tapped 0 times").assertExists()
        onNodeWithText("Reset").assertIsNotEnabled()
        onNodeWithText("Tap").performClick()
        onNodeWithText("Tapped 1 time").assertExists()
        onNodeWithText("Tap").performClick()
        onNodeWithText("Tapped 2 times").assertExists()
        onNodeWithText("Reset").performClick()
        onNodeWithText("Tapped 0 times").assertExists()
    }

    @Test
    fun showsThePlatformFromItsActual() = runComposeUiTest {
        setContent { MaterialTheme { ExamplePage() } }
        // Robolectric's SDK 34 reports Android 14.
        onNodeWithText("Running on Android 14").assertExists()
    }
}
