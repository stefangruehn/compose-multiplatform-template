package com.example.template

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** One choice of a setting, with a line on what it does. */
interface Choice {
    val title: StringResource
    val detail: StringResource
}

/** Light or dark, or as the phone is set. */
enum class Theme(override val title: StringResource, override val detail: StringResource) : Choice {
    System(Res.string.settings_theme_system, Res.string.settings_theme_system_detail),
    Light(Res.string.settings_theme_light, Res.string.settings_theme_light_detail),
    Dark(Res.string.settings_theme_dark, Res.string.settings_theme_dark_detail),
}

/** The page the app opens on. */
enum class Start(override val title: StringResource, override val detail: StringResource) : Choice {
    Home(Res.string.settings_start_home, Res.string.settings_start_home_detail),
    Last(Res.string.settings_start_last, Res.string.settings_start_last_detail),
}

/** Everything the user sets, kept between runs; [lastPage] is where [Start.Last] opens. */
data class Preferences(val theme: Theme = Theme.System, val start: Start = Start.Home, val lastPage: Page = Page.Home)

/** Material's default colours; pass your own to these builders to brand the app. */
private val AppLight = lightColorScheme()
private val AppDark = darkColorScheme()

/** Whether this theme is dark, given whether the system is; plain code, so the platform side can ask too. */
fun Theme.isDark(systemDark: Boolean): Boolean = when (this) {
    Theme.System -> systemDark
    Theme.Light -> false
    Theme.Dark -> true
}

/** The colour scheme [theme] stands for on this phone. */
@Composable
fun colorSchemeOf(theme: Theme): ColorScheme = if (theme.isDark(isSystemInDarkTheme())) AppDark else AppLight

/** Settings, grouped in cards. */
@Composable
fun SettingsPage(preferences: Preferences, onPreferences: (Preferences) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingsCard(stringResource(Res.string.settings_general), Res.drawable.tune) {
            ChoiceSetting(
                Res.drawable.home,
                stringResource(Res.string.settings_start),
                Start.entries,
                preferences.start,
            ) {
                onPreferences(preferences.copy(start = it))
            }
        }
        SettingsCard(stringResource(Res.string.settings_appearance), Res.drawable.visibility) {
            ChoiceSetting(
                Res.drawable.palette,
                stringResource(Res.string.settings_theme),
                Theme.entries,
                preferences.theme,
            ) {
                onPreferences(preferences.copy(theme = it))
            }
        }
    }
}

/** A setting with [icon] and [title] above its [choices] as radio buttons. */
@Composable
private fun <T : Choice> ChoiceSetting(
    icon: DrawableResource,
    title: String,
    choices: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    // The card spaces its content by 8 dp; the title and its choices keep that.
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.padding(start = SETTING_INDENT),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(icon, contentDescription = null)
            Text(title, style = MaterialTheme.typography.titleSmall)
        }
        Column(Modifier.padding(start = SETTING_INDENT * 2).selectableGroup()) {
            for (option in choices) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(option == selected, role = Role.RadioButton) { onSelect(option) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = option == selected, onClick = null)
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(stringResource(option.title), style = MaterialTheme.typography.bodyLarge)
                        Text(
                            stringResource(option.detail),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

/** How far a setting sits in from its card's title. */
private val SETTING_INDENT = 16.dp

@Composable
private fun SettingsCard(title: String, icon: DrawableResource, content: @Composable () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AppIcon(icon, contentDescription = null)
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            content()
        }
    }
}
