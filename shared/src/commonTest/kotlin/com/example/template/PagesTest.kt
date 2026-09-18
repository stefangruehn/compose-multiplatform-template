package com.example.template

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PagesTest {
    @Test
    fun splitsAtLevelTwoHeadings() {
        val (intro, sections) = sectionsOf(
            "Intro.\n\n## One\n\nFirst.\n\n### Sub\n\nStill first.\n\n## [0.2.0] - 2026-01-01\n\nSecond.\n",
        )
        assertEquals("Intro.", intro)
        assertEquals(
            listOf("One" to "First.\n\n### Sub\n\nStill first.", "0.2.0 - 2026-01-01" to "Second."),
            sections,
        )
    }

    @Test
    fun everyCardOnTheWrittenPagesHasItsOwnIcon() {
        val missing = listOf(Page.Help, Page.Imprint, Page.Privacy)
            .flatMap { page -> sectionsOf(textOf(page)).second.map { "${page.name}: ${it.first}" to it.first } }
            .filter { (_, title) -> title !in SECTION_ICONS }
            .map { it.first }
        assertTrue(missing.isEmpty(), "headings without an icon in SECTION_ICONS: $missing")
    }

    @Test
    fun theChangelogHasACardPerVersionAndNoIntro() {
        val (intro, sections) = sectionsOf(textOf(Page.Changelog))
        assertEquals("", intro)
        assertTrue(sections.isNotEmpty() && sections.all { it.first.first().isDigit() || it.first == "Unreleased" })
    }

    @Test
    fun backLeadsToTheGroupAbove() {
        assertEquals(Page.Home, Page.Example.parent)
        assertEquals(Page.About, Page.Privacy.parent)
        assertEquals(Page.Home, Page.Settings.parent)
    }
}
