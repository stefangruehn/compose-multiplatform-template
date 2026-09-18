# Compose Multiplatform template

A small Android app written with [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/),
meant to be copied and learned from. It has no features of its own, only the shell most apps need:

- a menu that slides in from the left, with pages grouped under **Home** and **About**,
- a page of cards for each group, and the back gesture leading to the page above,
- one **Example** page to copy: a counter and a line from platform-specific code,
- **Settings** for the theme (light, dark or as the phone is set) and the start page, kept between runs,
- **Help**, **Imprint**, **Privacy** and **Changelog** written in Markdown, and the list of
  **Libraries** with their licences.

Only Android is built. The code is laid out so that everything not tied to Android sits in a
shared module, ready for more targets.

## Build and run

You need JDK 21 or later and the Android SDK (API 37). Android Studio brings both; on the command
line, point `local.properties` at the SDK (`sdk.dir=/path/to/Android/Sdk`) or set `ANDROID_HOME`.
Gradle fetches the JDKs it runs and compiles with by itself (`gradle/gradle-daemon-jvm.properties`,
`jvmToolchain(21)`). Then:

```
./gradlew :androidApp:installDebug      # build and install on a connected phone or emulator
./gradlew :shared:testAndroidHostTest   # all tests, on the computer
./gradlew detekt :androidApp:lintDebug  # linters; detekt --auto-correct fixes formatting
./gradlew :androidApp:assembleRelease   # shrunk with R8, unsigned unless you add a key
```

The app runs on Android 10 (API 29) and later.

## Project structure

```
.
|-- androidApp/                  the Android app: a thin shell around shared/
|   |-- build.gradle.kts         application ID, version, R8, optional signing
|   |-- proguard-rules.pro       extra R8 rules, if a library needs them
|   `-- src/main/
|       |-- AndroidManifest.xml
|       |-- kotlin/.../MainActivity.kt   settings storage, back gesture, edge-to-edge
|       `-- res/                 launcher icon, app name, keep rule for the library list
|-- shared/                      everything that is not Android-specific
|   |-- build.gradle.kts         targets, dependencies, Markdown generator
|   `-- src/
|       |-- commonMain/
|       |   |-- kotlin/          App, Pages, Settings, ExamplePage, Buttons, Icons, Platform
|       |   |-- composeResources/ strings and icons (Material Symbols)
|       |   `-- markdown/        help.md, imprint.md, privacy.md
|       |-- androidMain/         the Android `actual` of Platform
|       |-- commonTest/          plain unit tests
|       `-- androidHostTest/     Compose UI tests under Robolectric
|-- config/detekt.yml            where the project departs from detekt's defaults, and why
|-- gradle/libs.versions.toml    every dependency version, in one place
|-- CHANGELOG.md                 shown in the app as the Changelog page
`-- .github/workflows/ci.yml     optional: the same checks on every push
```

How a page gets on screen:

```
MainActivity --(back, settings, library list)--> App
App: TopBar + Menu + PageContent(page)
PageContent: group (Home, About) -> CardsPage
             Settings            -> SettingsPage
             Example             -> ExamplePage
             Libraries           -> LibrariesContainer
             anything else       -> MarkdownPage (one card per "## " section)
```

## The practices it shows, and why

- **Shared module, thin `androidApp`.** Screens, state and logic live in `shared`; the app module only
  hands over what only Android has (storage, the back gesture, raw resources). Another target, such as
  desktop or iOS, then needs its own thin shell and nothing more.
- **`expect` / `actual`.** `Platform.kt` declares `platformName()`, `Platform.android.kt` implements it.
  This is how shared code reaches platform APIs without depending on them.
- **Compose resources instead of hard-coded text.** Every string and icon comes from
  `composeResources`, through the generated `Res` class. Translations become one more
  `values-<lang>/strings.xml`, and plurals (as on the Example page) come for free.
- **Platform pieces passed in as parameters.** `App` takes the back handler and the settings store as
  arguments instead of reaching for Android itself, which keeps it testable and portable.
- **Version catalog.** `gradle/libs.versions.toml` names every library and build tool once; the build
  files refer to them as `libs.…`, so an update is a one-line change.
- **detekt and Android lint.** detekt (with the ktlint and Compose rule sets) checks style and common
  Kotlin and Compose mistakes; lint checks the Android side. Lint treats warnings as errors, so they
  are fixed while they are few.
- **R8 in release.** The release build shrinks and obfuscates code and drops unused resources: a
  smaller APK, and a check that the code survives shrinking before users see it.
- **Host tests with Robolectric.** Compose UI tests run on the computer, without an emulator, so they
  are fast enough to run on every change. Plain logic is tested in `commonTest`.
- **Optional release signing.** The release build works without a key and is then unsigned; with a
  key it is signed. Nobody needs your secrets to build the project.
- **Markdown pages compiled in.** A small Gradle task turns each `.md` file into a Kotlin string, so
  text pages are edited as text and need no network.

## Signing a release

Create a keystore once, for example with
`keytool -genkeypair -v -keystore release.p12 -storetype PKCS12 -alias release -keyalg RSA -keysize 4096 -validity 10000`,
and keep it and its password outside the repository. Then give the build four values, either as
Gradle properties in `~/.gradle/gradle.properties`:

```
release.storeFile=/absolute/path/to/release.p12
release.storePassword=...
release.keyAlias=release
release.keyPassword=...
```

or as environment variables `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS` and
`RELEASE_KEY_PASSWORD`. `./gradlew :androidApp:assembleRelease` then signs the APK. Without them
it builds an unsigned APK, which you can sign later with `apksigner`.

## Make it your own

- [ ] **Package:** in Android Studio, right-click `com.example.template` and use
      *Refactor > Rename*; then change `appPackage` in `shared/build.gradle.kts` and `namespace` in
      `androidApp/build.gradle.kts` to match.
- [ ] **Application ID:** `applicationId` in `androidApp/build.gradle.kts`. It identifies the app on
      the phone and in stores and cannot change after the first release.
- [ ] **Name:** `app_name` in `androidApp/src/main/res/values/strings.xml` (launcher) and in
      `shared/src/commonMain/composeResources/values/strings.xml` (menu), and `rootProject.name`
      in `settings.gradle.kts`.
- [ ] **Launcher icon:** `ic_launcher_foreground.xml` and the colour in `ic_launcher_background.xml`.
- [ ] **Colours:** `AppLight` and `AppDark` in `Settings.kt`.
- [ ] **Pages:** replace the text in `help.md`, `imprint.md` and `privacy.md`; add an icon to
      `SECTION_ICONS` for every new `## ` heading (a test checks this).
- [ ] **Your first page:** copy `ExamplePage.kt`, add an entry to `Page` and to `GROUPS` in
      `Pages.kt`, and its strings.
- [ ] **Licence:** replace `LICENSE-MIT` and `LICENSE-APACHE` with your own licence.
- [ ] **Version:** `versionName` and `versionCode` in `androidApp/build.gradle.kts`, and a fresh
      `CHANGELOG.md`.

## Licence

Licensed under either of [Apache License, Version 2.0](LICENSE-APACHE) or
[MIT license](LICENSE-MIT) at your option.
