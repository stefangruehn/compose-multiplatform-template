package com.example.template

import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsTest {
    @Test
    fun onlyTheSystemThemeFollowsThePhone() {
        assertEquals(listOf(false, true), listOf(false, true).map { Theme.System.isDark(it) })
        assertEquals(listOf(false, false), listOf(false, true).map { Theme.Light.isDark(it) })
        assertEquals(listOf(true, true), listOf(false, true).map { Theme.Dark.isDark(it) })
    }
}
